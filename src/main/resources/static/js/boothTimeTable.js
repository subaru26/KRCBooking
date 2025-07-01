let boothList = [];
//ブースの項目追加用
(async () => {
	try {
		// ブース数取得
		const response = await fetch('https://krcbooking.onrender.com/api/booth/boothCount', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			}
		});
		const jsonResponse = await response.json();
		console.log(jsonResponse);

		boothList = jsonResponse.map(obj => {
			const parts = obj.boothPlace.split("_");
			return {
				facility: parts[0],
				detail: parts[1] + "_" + parts[2],
				boothNumber: obj.boothNumber
			};
		});

		console.log(boothList);

	} catch (e) {
		console.error("API取得時にエラー:", e);
	}
})();

loadReservationTable(0);

async function loadReservationTable(selectFacility) {
	try {
		const selectDate = localStorage.getItem("selectedDate");
		// 予約状況取得
		const response = await fetch('https://krcbooking.onrender.com/api/booth/reservationTime', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				selectDate: selectDate,
				selectFacility: selectFacility
			}),
		});
		const boothTimeTable = await response.json();
		console.log(boothTimeTable);
		const startHour = 9;
		const endHour = 16;
		const intervalsPerHour = 4;
		const tbody = document.getElementById("time-table-body");
		tbody.innerHTML = "";
		const theadRow = document.getElementById("time-table-head");
		theadRow.innerHTML = "";
		// ヘッダー
		const timeHeader = document.createElement("th");
		timeHeader.textContent = "時間\\ブース番号";
		timeHeader.classList.add("sticky-corner");
		theadRow.appendChild(timeHeader);

		const displayedBoothNumbers = [];

		for (let temporary of boothList) {
			const th = document.createElement("th");

			if (selectFacility + 1 == temporary.facility) {
				th.textContent = temporary.detail;
				theadRow.appendChild(th);
				displayedBoothNumbers.push(temporary.boothNumber);
			}
		}
		// タイムテーブル本体
		for (let hour = startHour; hour <= endHour; hour++) {
			for (let quarter = 0; quarter < intervalsPerHour; quarter++) {
				const minutes = quarter * 15;
				const paddedHour = hour.toString().padStart(2, "0");
				const paddedMinutes = minutes.toString().padStart(2, "0");
				const timeStr = `${paddedHour}:${paddedMinutes}:00`;
				const row = document.createElement("tr");
				const timeLabelCell = document.createElement("th");
				timeLabelCell.textContent = `${paddedHour}:${paddedMinutes}`;
				timeLabelCell.classList.add("sticky-left");
				row.appendChild(timeLabelCell);
				for (let boothIndex = 0; boothIndex < boothTimeTable.length; boothIndex++) {
					const cell = document.createElement("td");
					const boothSchedules = boothTimeTable[boothIndex];
					let isAvailable = false;
					for (const slot of boothSchedules) {
						if (slot.startTime <= timeStr && timeStr < slot.endTime) {
							isAvailable = true;
							break;
						}
					}
					cell.textContent = isAvailable ? "〇" : "×";
					cell.dataset.time = `${paddedHour}:${paddedMinutes}`;
					cell.dataset.booth = displayedBoothNumbers[boothIndex];
					if (isAvailable) {
						cell.classList.add("cell-available");
						cell.style.cursor = "pointer";
					} else {
						cell.classList.add("cell-unavailable");
						cell.style.pointerEvents = "none";
						cell.style.cursor = "default";
					}
					row.appendChild(cell);
				}
				tbody.appendChild(row);
			}
		}
	} catch (e) {
		console.error("API取得時にエラー:", e);
	}
}
// 日付表示
const beforeDate = localStorage.getItem("selectedDate");
if (beforeDate) {
	const DisplayDateArray = beforeDate.split("-");
	const displayDate = DisplayDateArray[1] + "月" + DisplayDateArray[2] + "日";
	document.getElementById("selectText").textContent = displayDate;
}
// セル選択ロジック
const selectTime = [];
let selectedBoothNum = null;
document.addEventListener("DOMContentLoaded", () => {
	const tbody = document.getElementById("time-table-body");
	tbody.addEventListener("click", (e) => {
		const target = e.target;
		if (target.tagName === "TD" && target.textContent === "〇") {
			const time = target.dataset.time;
			const booth = target.dataset.booth;

			// まず、同じセルがすでに選択されているか判定し、選択解除処理を最初に行う
			const index = selectTime.findIndex(
				(item) => item.time === time && item.booth === booth
			);

			if (index !== -1) {
				// 選択済みセルを再クリック → 選択解除
				selectTime.splice(index, 1);
				target.classList.remove("selected-cell");

				if (selectTime.length === 0) {
					selectedBoothNum = null;
					document.getElementById("bookingButton").style.display = "none";
					console.log(selectTime);
					return;  // 選択なしならモーダル不要なので早期リターン
				}

				// 選択解除後の同じブースの残り選択時間だけ抽出
				const toMinutes = (t) => {
					const [h, m] = t.split(":").map(Number);
					return h * 60 + m;
				};
				const timesAfterDelete = selectTime
					.filter(item => item.booth === booth)
					.map(item => toMinutes(item.time))
					.sort((a, b) => a - b);

				// 連続判定
				for (let i = 0; i < timesAfterDelete.length - 1; i++) {
					if (timesAfterDelete[i + 1] - timesAfterDelete[i] !== 15) {
						// 連続じゃなければモーダル表示
						document.getElementById("timeSkippedModal").style.display = "block";

						document.getElementById("skippedChange").onclick = () => {
							// 前の選択をすべて解除
							selectTime.length = 0;
							document.querySelectorAll(".selected-cell").forEach(cell => {
								cell.classList.remove("selected-cell");
							});
							// 新しい選択に切り替え
							selectTime.push({ time, booth });
							target.classList.add("selected-cell");
							selectedBoothNum = booth;
							document.getElementById("bookingButton").style.display = "inline-block";
							document.getElementById("timeSkippedModal").style.display = "none";
							console.log(selectTime);
						};
						document.getElementById("cancelSkipped").onclick = () => {
							document.getElementById("timeSkippedModal").style.display = "none";
						};
						return;
					}
				}

				console.log(selectTime);
				return;
			}

			// 新規選択時の連続判定用関数
			const toMinutes = (t) => {
				const [h, m] = t.split(":").map(Number);
				return h * 60 + m;
			};
			const newTimeMin = toMinutes(time);

			// 同じブースの選択済み時間だけ取り出す
			const sameBoothTimes = selectTime
				.filter(item => item.booth === booth)
				.map(item => toMinutes(item.time))
				.sort((a, b) => a - b);

			const isContinuous = () => {
				if (sameBoothTimes.length === 0) return true; // 最初ならOK
				// 連続しているかどうか判定
				for (const t of sameBoothTimes) {
					if (Math.abs(t - newTimeMin) === 15) return true;
				}
				return false;
			};

			// 連続チェック
			if (!isContinuous()) {
				document.getElementById("timeSkippedModal").style.display = "block";

				document.getElementById("skippedChange").onclick = () => {
					// 前の選択をすべて解除
					selectTime.length = 0;
					document.querySelectorAll(".selected-cell").forEach(cell => {
						cell.classList.remove("selected-cell");
					});
					// 新しい選択に切り替え
					selectTime.push({ time, booth });
					target.classList.add("selected-cell");
					selectedBoothNum = booth;
					document.getElementById("bookingButton").style.display = "inline-block";
					document.getElementById("timeSkippedModal").style.display = "none";
					console.log(selectTime);
				};
				// 「キャンセル」クリック時の処理
				document.getElementById("cancelSkipped").onclick = () => {
					document.getElementById("timeSkippedModal").style.display = "none";
				};
				return;
			}

			// 違うブース選択時モーダル表示
			if (selectedBoothNum !== null && booth !== selectedBoothNum) {
				document.getElementById("columnAlertModal").style.display = "block";
				document.getElementById("confirmChange").onclick = () => {
					selectTime.length = 0;
					document.querySelectorAll(".selected-cell").forEach(cell => {
						cell.classList.remove("selected-cell");
					});
					selectTime.push({ time, booth });
					target.classList.add("selected-cell");
					selectedBoothNum = booth;
					document.getElementById("bookingButton").style.display = "inline-block";
					document.getElementById("columnAlertModal").style.display = "none";
					console.log(selectTime);
				};
				document.getElementById("cancelChange").onclick = () => {
					document.getElementById("columnAlertModal").style.display = "none";
				};
				return;
			}

			if (selectedBoothNum == null) {
				selectedBoothNum = booth;
			}

			// 新規選択追加
			selectTime.push({ time, booth });
			target.classList.add("selected-cell");

			const bookingButton = document.getElementById("bookingButton");
			if (selectTime.length > 0) {
				bookingButton.style.display = "inline-block";
			} else {
				bookingButton.style.display = "none";
			}
			console.log(selectTime);
		}
	});
});

const studentId = localStorage.getItem("studentId");
const resultArray = [];
document.getElementById("bookingForm").addEventListener("submit", (e) => {
	const input = document.getElementById("reservationData");
	selectTime.sort((a, b) => {
		if (a.time < b.time) return -1;
		if (a.time > b.time) return 1;
		return 0;
	});
	resultArray.push(selectTime);
	resultArray.push({ selectedDate: localStorage.getItem("selectedDate") });
	resultArray.push({ studentId: studentId });
	input.value = JSON.stringify(resultArray);
	
	localStorage.setItem("selectedReservations", JSON.stringify(selectTime));
});

// モーダルの要素取得
const modal = document.getElementById("selectTimeModal");
const closeBtn = modal.querySelector(".close");
const showBtn = document.getElementById("showSelectedBtn");
const time = document.getElementById("selectTime");
const check = document.getElementById("checkTextId");

// モーダル開く
showBtn.addEventListener("click", () => {
	time.innerHTML = "";
	check.innerHTML = "";
	if (selectTime.length === 0) {
		const li = document.createElement("p");
		li.textContent = "まだ選択されていません。";
		time.appendChild(li);
	} else {
		selectTime.sort((a, b) => {
			if (a.time < b.time) return -1;
			if (a.time > b.time) return 1;
			return 0;
		});
		const startTime = selectTime[0].time;
		const endTime = addMinutesToTime(selectTime[selectTime.length - 1].time, 15);
		const li = document.createElement("p");
		li.textContent = `${startTime} ～ ${endTime}`;
		time.appendChild(li);
		const li2 = document.createElement("p");
		li2.textContent = '上記時間でよろしいですか？？';
		check.append(li2);
		
		// 「キャンセル」クリック時の処理
		document.getElementById("cancelBooking").onclick = () => {
			document.getElementById("selectTimeModal").style.display = "none";
		};
	}
	modal.style.display = "block";
});

// 閉じるボタン
closeBtn.addEventListener("click", () => {
	modal.style.display = "none";
});
// モーダル外クリックで閉じる
window.addEventListener("click", (e) => {
	if (e.target === modal) {
		modal.style.display = "none";
	}
});

function addMinutesToTime(timeStr, minutesToAdd) {
	const [hourStr, minStr] = timeStr.split(":");
	let hour = parseInt(hourStr, 10);
	let minute = parseInt(minStr, 10);
	minute += minutesToAdd;
	if (minute >= 60) {
		hour += Math.floor(minute / 60);
		minute = minute % 60;
	}
	return hour.toString().padStart(2, "0") + ":" + minute.toString().padStart(2, "0");
}

function showTab(index) {
	const tabs = document.querySelectorAll('.folder-tab');
	const tables = document.querySelectorAll('.table-container');

	tabs.forEach((tab, i) => {
		tab.classList.toggle('active', i === index);
	});

	tables.forEach((table, i) => {
		table.style.display = (i === index) ? 'block' : 'none';
	});

	// ★ 前の選択セルをクリア
	document.querySelectorAll(".selected-cell").forEach(cell => {
		cell.classList.remove("selected-cell");
	});

	// ★ 選択データ初期化
	selectTime.length = 0;
	selectedBoothNum = null;

	// ★ ボタン非表示にする（再選択まで）
	const bookingButton = document.getElementById("bookingButton");
	if (bookingButton) {
		bookingButton.style.display = "none";
	}

	// ★ タイムテーブル再読込
	loadReservationTable(index);
}
