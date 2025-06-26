let currentYear = new Date().getFullYear();
let currentMonth = new Date().getMonth();

let selectedYear, selectedMonth, selectedDate;

document.getElementById("prev-month").addEventListener("click", () => {
	const today = new Date();
	const targetDate = new Date(currentYear, currentMonth - 1);

	if (targetDate.getFullYear() < today.getFullYear() ||
		(targetDate.getFullYear() === today.getFullYear() && targetDate.getMonth() < today.getMonth())) {
		// 過去には移動できない
		return;
	}

	currentMonth--;
	if (currentMonth < 0) {
		currentMonth = 11;
		currentYear--;
	}
	generateCalendar(currentYear, currentMonth);
});


document.getElementById("next-month").addEventListener("click", () => {
	currentMonth++;
	if (currentMonth > 11) {
		currentMonth = 0;
		currentYear++;
	}
	generateCalendar(currentYear, currentMonth);
});

function generateCalendar(year, month) {
	const monthYearText = `${year}年${month + 1}月`;

	const prevButton = document.getElementById("prev-month");
	const today = new Date();
	today.setHours(0, 0, 0, 0);

	const isCurrentMonth = year === today.getFullYear() && month === today.getMonth();

	if (isCurrentMonth) {
		prevButton.style.display = "none";
	} else {
		prevButton.style.display = "inline-block";
	}

	document.getElementById("month-year").textContent = monthYearText;
	const firstDay = new Date(year, month, 1).getDay();
	const lastDate = new Date(year, month + 1, 0).getDate();
	const tbody = document.getElementById("calendar-body");
	tbody.innerHTML = "";

	let date = 1;
	for (let i = 0; i < 6; i++) {
		const row = document.createElement("tr");
		for (let j = 0; j < 7; j++) {
			const cell = document.createElement("td");
			cell.style.border = "1px solid #ccc";
			cell.style.padding = "10px";
			cell.style.textAlign = "center";

			if (i === 0 && j < firstDay) {
				cell.textContent = "";
			} else if (date > lastDate) {
				break;
			} else {
				const currentDate = date; // date++する前に固定
				cell.textContent = currentDate;

				const cellDate = new Date(year, month, currentDate);
				cellDate.setHours(0, 0, 0, 0);

				if (cellDate < today) {
					// 過去の日付は無効
					cell.style.color = "#ccc";
					cell.style.backgroundColor = "#f9f9f9"; // ← これが背景色
					cell.style.cursor = "default";
				} else {
					cell.classList.add("date-cell");
					cell.style.cursor = "pointer";
					cell.addEventListener("click", () => openReservationForm(year, month, currentDate));
				}

				date++;
			}
			row.appendChild(cell);
		}
		tbody.appendChild(row);
		if (date > lastDate) break;
	}
}

function openReservationForm(year, month, date) {
	const selectedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}`;
	localStorage.setItem("selectedDate", selectedDate);

	const form = document.createElement('form');
	form.method = 'POST';
	form.action = '/boothTimetable';

	document.body.appendChild(form);
	form.submit();
}



generateCalendar(currentYear, currentMonth);