![image](https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/4ba1e7fc-06ac-4c98-9f2f-e34744e888a2)
# <div align="center">01-doo-re-back</div> 
<div align="center">BDD 1기 프로젝트 두레(DOORE)의 Backend Repository입니다.</div>
<br>


두레(DOORE)는 **팀이 함께 협업하여 다른 팀과 경쟁하기 위한 팀간 경쟁 서비스**로, 주민들이 마을 단위로 둔 공동 노동 조직 '**두레**'에서 이름을 따왔습니다.

- **경쟁**: Github의 잔디를 모티브로 한 '텃밭'이 팀별로 존재 하며, 이를 지표로 팀 랭킹이 매깁니다.
- **협업**: 팀원들은 스터디, 학습자료 업로드 등의 활동을 통해 팀의 '텃밭'에 기여할 수 있습니다.

<br>
운영서버: https://doore.pnu.app/ <br>
Rest Docs: https://doore.pnu.app/api/docs/doore.html
<br><br>

## 🧑🏻‍💻 코드 유지 관리자

<div align="left">
  <table>
  <tr>
    <td align="center">
      이지민
    </td>    
    <td align="center">
      이서연
    </td>
    <td align="center">
      신예준
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/JJimini">
        <img src="https://github.com/JJimini.png" width="80" alt="JJimini"/>
        <br/>
        <sub><b>JJimini</b></sub>
      </a>
      <br/>
    </td>
    <td align="center">
      <a href="https://github.com/yeonddori">
      <img src="https://github.com/yeonddori.png" width="80" alt="yeonddori"/>
      <br />
      <sub><b>yeonddori</b></sub>
      </a>
      <br/>
    </td>
    <td align="center">
      <a href="https://github.com/yessjun">
      <img src="https://github.com/yessjun.png" width="80" alt="yessjun"/>
      <br />
      <sub><b>yessjun</b></sub>
      </a>
      <br/>
    </td>
  </tr>
</table>
</div>

### 🤝기여자

[![contributors](https://contrib.rocks/image?repo=BDD-CLUB/01-doo-re-front)](https://github.com/BDD-CLUB/01-doo-re-front/graphs/contributors) [![contributors](https://contrib.rocks/image?repo=BDD-CLUB/01-doo-re-back)](https://github.com/BDD-CLUB/01-doo-re-back/graphs/contributors)  
<br>

## :pencil2: 기술 스택

<div align="center">

**Language**

![Jdk 17](https://img.shields.io/badge/-Jdk%2017-437291?style=for-the-badge&logo=openjdk&logoColor=white)
![Java](https://img.shields.io/badge/-Java-8D6748?style=for-the-badge)

**Dependancy**

![Spring Boot 3.2.1](https://img.shields.io/badge/Spring%20boot%203.2.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Rest Docs](https://img.shields.io/badge/Spring%20rest%20docs-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data Jpa](https://img.shields.io/badge/Spring%20data%20jpa-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-be2e22?style=for-the-badge&logo=lombok&logoColor=white)
![Spring Web](https://img.shields.io/badge/Spring%20Web-be2e22?style=for-the-badge&logo=springboot&logoColor=white)
![Validation](https://img.shields.io/badge/Validation-be2e22?style=for-the-badge&logo=validation&logoColor=white)
![Jwt](https://img.shields.io/badge/Jwt-181717?style=for-the-badge&logo=jwt&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/-Mockito-6DB33F?style=for-the-badge)

**Database**

![Mysql 8.0](https://img.shields.io/badge/MySQL%208.0-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)

**Tool**

![Gradle](https://img.shields.io/badge/Gradle%207.6-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![IntelliJ](https://img.shields.io/badge/IntelliJ-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
</div>
<br>

## :desktop_computer: Structure
```
─ src
   ├─ main
   │  ├─ java
   │  │  └─ doore
   │  │     ├─ DooreApplication.java
   │  │     ├─ base
   │  │     │  ├─ BaseEntity.java
   │  │     │  ├─ BaseException.java
   │  │     │  └─ BaseExceptionType.java
   │  │     ├─ config
   │  │     ├─ document
   │  │     ├─ exception
   │  │     ├─ file
   │  │     ├─ garden
   │  │     ├─ login
   │  │     ├─ member
   │  │     ├─ resolver
   │  │     ├─ study
   │  │     ├─ team
   │  │     │  ├─ api
   │  │     │  ├─ application
   │  │     │  │  ├─ convenience
   │  │     │  │  └─ dto
   │  │     │  ├─ domain
   │  │     │  │  └─ repository
   │  │     │  └─ exception
   │  │     │     ├─ TeamException.java
   │  │     │     └─ TeamExceptionType.java
   │  │     └─ util
   │  └─ resources
   │     ├─ application.yml
   │     └─ schema.sql
   └─ test
```
<br>

## :seedling: Features

### 소셜 로그인
- 구글 계정을 통한 소셜 로그인을 지원합니다. 구글 계정만 있다면 누구나 두레에 가입할 수 있습니다.  
<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>소셜 로그인</strong>
    </td>    
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/3df8a758-f636-4c41-921d-be50a76276ca"/>
    </td>
  </tr>
  </table>
</div>
<br>

### 팀 랭킹
- **텃밭**: Github의 잔디를 모티브로 한 '텃밭'이 팀별로 주어지며, 이를 지표로 팀 랭킹이 매겨집니다.
- **협업**: 팀원들은 스터디, 학습자료 업로드 등의 팀 활동을 통해 팀의 '텃밭'에 기여할 수 있습니다.
- 1위부터 10위까지의 팀이 메인 페이지에 표시됩니다.
### 팀원
- **팀원 목록**: 해당 팀에 참여하고 있는 회원 목록을 제공합니다.
- **초대**: 팀장은 초대 버튼을 통해 팀에 새로운 회원을 초대할 수 있습니다.
### 팀 활동
- **스터디**: 팀에서 진행중인 스터디 목록과 그 진행률을 확인할 수 있습니다.
### 팀 학습자료
- **학습자료**: 팀에 업로드된 학습자료 목록을 확인할 수 있습니다.
- 팀 공개 학습자료는 팀원만 확인 가능하며, 전체 공개 학습자료는 회원이라면 모두 확인할 수 있습니다.

<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>팀 랭킹</strong>
    </td>    
    <td align="center">
      <strong>팀 활동</strong>
    </td>   
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/6d66fb5d-1d06-472a-bc42-909501e20a53"/>
    </td>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/e93c7626-955d-4519-962f-839c45d640a4"/>
    </td>
  </tr>
  </table>
</div>
<br>

### 스터디
- **참여자 목록**: 해당 스터디에 참여하고 있는 회원 목록을 제공합니다.
- **스터디 진행률**: 모든 팀원의 진척도를 합산하여 스터디 진행률을 계산합니다. 스터디 진행률에 따라 작물 일러스트가 성장합니다. 새로 들어온 스터디원이 있다면 반영해서 진행률이 조정됩니다.
- **스터디 진행 기간**: 스터디가 진행되는 기간을 확인할 수 있습니다.
- **스터디 커리큘럼**: 개인별로 진행한 커리큘럼을 완료 처리, 커리큘럼 내용 수정, 커리큘럼 삭제, 커리큘럼의 순서를 바꿀 수 있습니다. 커리큘럼 관리는 스터디장만 가능합니다.
<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>스터디</strong>
    </td>    
    <td align="center">
      <string>커리큘럼 수정</string>
    </td>
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/4f351a69-dea3-4c97-aa1d-c2313c8a2558"/>
    </td>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/68d0eb34-0db9-450f-9e72-673e9a2083ed"/>
    </td>
  </tr>
  </table>
</div>
<br>

### 학습자료
- **학습자료**: 학습자료는 팀 학습자료와 스터디 학습자료로 구분되며, 스터디 학습자료는 스터디원만 열람할 수 있습니다.
  - 학습자료는 이미지, 파일, 링크의 형태로 업로드 가능합니다.
<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>학습자료-이미지</strong>
    </td>    
    <td align="center">
      <strong>학습자료-파일</strong>
    </td>    
    <td align="center">
      <strong>학습자료-링크</strong>
    </td>    
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/e796a246-8f88-457d-b7d8-b1cf23b1cd6c"/>
    </td>
    <td>
    <img width="600" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/655614d7-b3d4-439f-8155-281d29067f6a">
    </td>
    <td>
    <img width="600" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/28c7db00-f31b-4a0b-aed0-4f4401873fc2">
    </td>
  </tr>
  </table>
</div>
<br>

### 마이페이지

- **나의 스터디**: 소속된 스터디를 확인 가능합니다. 진행중인 스터디와 종료된 스터디를 나누어 확인할 수 있습니다.
- **나의 학습자료**: 내가 업로드한 학습자료를 확인할 수 있습니다.

<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>마이페이지</strong>
    </td>    
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/71449deb-8bcb-4ea3-900c-8fef86b930d8"/>
    </td>
  </tr>
  </table>
</div>
<br>

### 스터디 카드

- **스터디 카드**: 종료된 스터디에서의 나의 활동을 정리된 레이아웃으로 제공합니다.
  - 스터디에서의 커리큘럼을 확인할 수 있습니다.
  - 나의 진행률을 확인할 수 있습니다. 
  - 스터디에서 내가 업로드한 학습자료만 모아 확인할 수 있습니다.
- **스터디 카드 공유**: 스터디 카드를 정적 페이지로 공유하여 포토폴리오로 사용할 수 있습니다. 
  - 공유하고 싶은 스터디 카드를 선택할 수 있습니다.
  
<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>스터디 카드</strong>
    </td>    
    <td align="center">
      <strong>스터디 카드 외부 공유</strong>
    </td>  
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/bb8e650c-179e-426b-b652-dcf806154e6b"/>
    </td>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/ab57de7b-7c31-480b-ac12-4bedacf62b69"/>
    </td>
  </tr>
  </table>
</div>
<br>

## 🌐 Infra Structure


## 💾 Database
- FK를 사용하지 않는 전략을 선택했습니다.
- [schema.sql 자세히 보기](https://github.com/BDD-CLUB/01-doo-re-back/blob/develop/src/main/resources/schema.sql)
  
<div align="center">
  <table>
  <tr>
    <td>
    <img width="600px" src="https://github.com/user-attachments/assets/2e30de85-b157-4e73-b1be-d56640218931"/>
    </td>
  </tr>
  </table>
</div>
<br>
