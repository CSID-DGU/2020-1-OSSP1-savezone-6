# 2020-1-OSSP1-savezone-6

***

![title](./img/title.jpg)

# " 똑똑한 소비 습관, SmartCart "

***

### ✔ Description

**단 한 번의 사진 촬영을 통한 유통기한 관리 애플리케이션**

### ✔ 기능

* **유통기한이 명시되어 있지 않은 식품 (ex:채소, 과일)에 대한** 적절한 보관 방법과 섭취 가능 기간을 함께 제공
* **유통기한이 명시되어 있는 유제품에 대한** 유통기한 인식 및 보관

### ✔ 개발 도구

* **Android Studio**
* **Tensorflow**
* **Keras**
* **Tensorflow lite**
* **Keras-retinanet**
* **realm database**

### ✔ Classification Model1
* **물품 정보 인식
* **number of class : 7*
* **input :  [1 224 224 3]*
* **output : [1 7]*
* **average accuracy : 90 +-3%**

### Detection Model
* **정확도가 낮아 실제 사용할 정도의 정확도가 나오지 않은 상황임 
* **추가적인 데이터 학습을 진행해 모델의 정확도를 높여야 함.

### Classification Model2
* **Detection Model로 인식한 유통기한 정보로부터 유통기한 정보를 읽어오는 모델
* **구현 예정

### ✔ TEAM Info

* **지도교수 : 손윤식 교수님**
* **팀명 : 세이브존**
* **팀원 : 정일용, 김완기, 하이현**

