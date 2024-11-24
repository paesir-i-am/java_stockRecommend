# 자바 주식 추천 및 검색 프로그램
## 사용 기능

💻 Mac OS

🛠️ Java

      UI : Swing
      Graph : JFreeChart
      Data : Json
      Scrap : Jsoup

📒 API

      KRX 한국거래소 API
----

## 클래스 다이어그램

![classDiagram](https://github.com/user-attachments/assets/54f37e21-f385-408d-be60-2e892ba2d969)

----
    
## 기능 구현

1. 종목 추천 : 3개월의 데이터 분석 후 점수 반영 후 4개의 상위 종목 추천
2. 뉴스 데이터 스크래핑 : 네이버 뉴스 탭의 주식 관련 기사 스크래핑 후 메인 화면 하단 타이틀 및 내용 표기 및 링크
3. 데이터 검색 구현 : 검색창에 검색어 검색 시 관련 검색어 출현, 종목명 및 종목코드로 검색 가능 
----

### 1. 종목 추천

MainApp 실행 시 UI 중간 섹션에 AI 추천 주식으로 그래프 표현

<img width="100%" alt="SCR-20241124-tioy" src="https://github.com/user-attachments/assets/ee4b6adf-076b-4ac5-ab49-c2ec902c077a">

----
Code Flow : DataStorage 에서 데이터 조회 후 없으면 AuthClient의 API 를 통해 조회 및 파일로 저장 

-> 파일의 3개월의 데이터를 평균치로 계산하여 스코어링 후 상위 4개 종목 시가 그래프로 표현 


<img width="48%" alt="SCR-20241124-tjla" src="https://github.com/user-attachments/assets/8d267370-0e13-4844-bc56-1df54a92d52d">
<img width="48%" alt="SCR-20241124-tjtt" src="https://github.com/user-attachments/assets/8a1029f4-d121-4e9b-8c96-876e27409038">

----

<img width="48%" alt="SCR-20241124-tkfx" src="https://github.com/user-attachments/assets/0c39e465-d217-4547-9c37-4002961ecdea">
<img width="48%" alt="SCR-20241124-tkas" src="https://github.com/user-attachments/assets/370e9131-5ac5-4399-96ae-caab5444a303">

----

<img width="100%" alt="SCR-20241124-tthr" src="https://github.com/user-attachments/assets/109dcfc9-d572-439f-bb21-6b6fe2bc11af">

----


### 2. 뉴스 데이터 스크래핑

네이버 뉴스 탭의 기사 스크래핑하여 메인 화면에 타이틀 및 내용 출력하고 기사를 클릭하면 해당 기사 링크로 이동하는 기능 구현

<img width="48%" alt="SCR-20241124-tuxc" src="https://github.com/user-attachments/assets/139045c6-ece4-4a00-9fd9-3570a9c3dc97">
<img width="48%" alt="SCR-20241124-tved" src="https://github.com/user-attachments/assets/bb805012-55d1-48b5-95c9-2a153811e2e6"> 

----

### 3. 데이터 검색 구현

검색어 검색 시 연관 검색어 구현 및 종목명뿐 아니라 종목코드로도 검색 가능하도록 구현

검색어 미 완성 검색시 관련 검색어 페이지네이션 구현

<img width="100%" alt="SCR-20241124-twww" src="https://github.com/user-attachments/assets/ff769c17-3cd3-4999-9834-64a853a5f4f0">

----

<img width="100%" alt="SCR-20241124-txus" src="https://github.com/user-attachments/assets/8a7ce0da-d5f5-4077-bb21-e524c9301675">

----

<img width="48%" alt="SCR-20241124-tzta" src="https://github.com/user-attachments/assets/2dc5937d-fd22-43f2-a489-b96b9b99694c">
<img width="48%" alt="SCR-20241124-tzmk" src="https://github.com/user-attachments/assets/bc3d837e-75ba-4cc5-a792-049c7d747899">


----

## 마무리

### 아쉬운점

1. 코드의 실행이 매번 데이터를 불러와서 fetch data 의 유무를 확인 후 실행되다보니 초기 구동 시간이 오래 걸리는 편인데, 이 부분을 개선하면 더 좋을 듯 하다
2. UI 개선의 아쉬움이 있다. 자바로만 진행하다보니 프론트나 부트스트랩 등 기타 기능을 안쓰고 Swing 이나 JfreeChart 등의 기능만을 이용하다보니 UI 의 아쉬움이 있다


### 좋았던점

1. API 를 properties 에 따로 보관하고 Auth 인증 및 Config 등을 분리 운용하면서 보안의 중요성을 알았다
2. 데이터의 저장을 통해 추후 다른 곳에도 데이터를 써서 활용할 수 있다는 점이 좋았고 백엔드 첫 프로젝트였는데 생각보다 기능구현의 부분이 잘 되어 좋았다
3. 데이터 크롤링 또한 처음 구현해보았는데 추후에 다른 곳에도 활용할 수 있을 것 같다
