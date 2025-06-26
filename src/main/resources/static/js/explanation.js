// カラオケ説明スワイプ制御（強化版）

let karaokeContainer = document.getElementById('karaokeExplanation');
let karaokeCurrentIndex = 0;

let karaokeStartX = 0;
let karaokeStartY = 0;
let karaokeStartTime = 0;

karaokeContainer.addEventListener('touchstart', (e) => {
	karaokeStartX = e.touches[0].clientX;
	karaokeStartY = e.touches[0].clientY;
	karaokeStartTime = Date.now();
});

karaokeContainer.addEventListener('touchend', (e) => {
	let karaokeEndX = e.changedTouches[0].clientX;
	let karaokeEndY = e.changedTouches[0].clientY;
	let karaokeEndTime = Date.now();

	let deltaX = karaokeEndX - karaokeStartX;
	let deltaY = karaokeEndY - karaokeStartY;
	let elapsed = karaokeEndTime - karaokeStartTime;

	// 縦方向 or スワイプ距離・時間条件
	if (Math.abs(deltaX) < Math.abs(deltaY) || Math.abs(deltaX) < 50 || elapsed > 1000) {
		return;
	}

	// スワイプ方向でインデックス更新
	if (deltaX < 0) {
		karaokeCurrentIndex = Math.min(karaokeCurrentIndex + 1, karaokeContainer.children.length - 1);
	} else {
		karaokeCurrentIndex = Math.max(karaokeCurrentIndex - 1, 0);
	}

	// スクロールして表示更新
	karaokeContainer.scrollTo({
		left: karaokeCurrentIndex * window.innerWidth,
		behavior: 'smooth'
	});

	updateKaraokeIndicator();
});

// インジケーター表示更新
function updateKaraokeIndicator() {
  const totalSlides = karaokeContainer.children.length;
  const indicatorContainer = document.getElementById('karaokeIndicator');

  // まず中身クリア
  indicatorContainer.innerHTML = '';

  for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('span');
    dot.textContent = '●';
    dot.style.marginRight = '6px';
    dot.style.fontSize = '24px';
    dot.style.color = (i === karaokeCurrentIndex) ? '#d3d3d3' /* 薄いグレー */ : '#ffffff'; // 薄いグレーとさらに薄いグレー
    dot.style.textShadow = '0 0 2px black';

    indicatorContainer.appendChild(dot);
  }
}


// 初期表示（ページ読み込み時）
document.addEventListener('DOMContentLoaded', () => {
	updateKaraokeIndicator();
});



//ブース説明
let boothContainer = document.getElementById('boothExplanation');
let boothCurrentIndex = 0;

let boothStartX = 0;
let boothStartY = 0;
let boothStartTime = 0;

boothContainer.addEventListener('touchstart', (e) => {
	boothStartX = e.touches[0].clientX;
	boothStartY = e.touches[0].clientY;
	boothStartTime = Date.now();
});

boothContainer.addEventListener('touchend', (e) => {
	let boothEndX = e.changedTouches[0].clientX;
	let boothEndY = e.changedTouches[0].clientY;
	let boothEndTime = Date.now();

	let deltaX = boothEndX - boothStartX;
	let deltaY = boothEndY - boothStartY;
	let elapsed = boothEndTime - boothStartTime;

	// 縦方向 or スワイプ距離・時間条件
	if (Math.abs(deltaX) < Math.abs(deltaY) || Math.abs(deltaX) < 50 || elapsed > 1000) {
		return;
	}

	// スワイプ方向でインデックス更新
	if (deltaX < 0) {
		boothCurrentIndex = Math.min(boothCurrentIndex + 1, boothContainer.children.length - 1);
	} else {
		boothCurrentIndex = Math.max(boothCurrentIndex - 1, 0);
	}

	// スクロールして表示更新
	boothContainer.scrollTo({
		left: boothCurrentIndex * window.innerWidth,
		behavior: 'smooth'
	});

	updateBoothIndicator();
});

// インジケーター表示更新
function updateBoothIndicator() {
  const totalSlides = boothContainer.children.length;
  const indicatorContainer = document.getElementById('boothIndicator');

  // まず中身クリア
  indicatorContainer.innerHTML = '';

  for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('span');
    dot.textContent = '●';
    dot.style.marginRight = '6px';
    dot.style.fontSize = '24px';
    dot.style.color = (i === boothCurrentIndex) ? '#d3d3d3' /* 薄いグレー */ : '#ffffff'; // 薄いグレーとさらに薄いグレー
    dot.style.textShadow = '0 0 2px black';

    indicatorContainer.appendChild(dot);
  }
}


// 初期表示（ページ読み込み時）
document.addEventListener('DOMContentLoaded', () => {
	updateBoothIndicator();
});