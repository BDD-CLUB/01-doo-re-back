![image](https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/4ba1e7fc-06ac-4c98-9f2f-e34744e888a2)
# <div align="center">01-doo-re-back</div> 
<div align="center">BDD 1기 프로젝트 두레(DooRe)의 백엔드 레파지토리입니다.</div>
<br>


두레(DooRe)는 **팀이 함께 협업하여 다른 팀과 경쟁하기 위한 팀간 경쟁 서비스**로, 주민들이 마을 단위로 둔 공동 노동 조직 '**두레**'에서 이름을 따왔습니다.

- **경쟁**: Github의 잔디를 모티브로 한 '텃밭'이 팀별로 존재 하며, 이름 지표로 팀 랭킹이 매겨집니다.
- **협업**: 팀원들은 스터디, 학습자료 업로드 등의 활동을 통해 팀의 '텃밭'에 기여할 수 있습니다.
- **포토폴리오**: '스터디 카드'를 통해 회원이 참여했던 스터디에서 개인이 착습한 내용과 업로드한 학습 자료를 정리된 레이아웃으로 제공합니다.

<br>
운영서버: https://www.doore.kro.kr <br>
Rest Docs: https://www.doore.kro.kr/docs/doore.html
<br><br>

## 🧑🏻‍💻 기획 & 개발
<div align="center">
  <table>
  <tr>
    <td align="center">
      임연후
    </td>    
    <td align="center">
      이수빈
    </td>
    <td align="center">
      손현경
    </td>
    <td align="center">
      송세연
    </td>
    <td align="center">
      이지민 
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/lcqff">
        <img src="https://github.com/lcqff.png" width="80" alt="lcqff"/>
        <br/>
        <sub><b>lcqff</b></sub>
      </a>
      <br/>
    </td>
    <td align="center">
      <a href="https://github.com/02ggang9">
      <img src="https://github.com/02ggang9.png" width="80" alt="02ggang9"/>
      <br />
      <sub><b>02ggang9</b></sub>
      </a>
      <br/>
    </td>
    <td align="center">
      <a href="https://github.com/shkisme">
      <img src="https://github.com/shkisme.png" width="80" alt="shkisme"/>
      <br />
      <sub><b>shkisme</b></sub>
      </a>
      <br/>
    </td>
        <td align="center">
      <a href="https://github.com/amaran-th">
      <img src="https://github.com/amaran-th.png" width="80" alt="amaran-th"/>
      <br />
      <sub><b>amaranth</b></sub>
      </a>
      <br/>
    </td>
        <td align="center">
      <a href="https://github.com/JJimini">
      <img src="https://github.com/JJimini.png" width="80" alt="JJimini"/>
      <br />
      <sub><b>JJimini</b></sub>
      </a>
      <br/>
    </td>
  </tr>
</table>

</div>

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
   │  │     ├─ crop
   │  │     ├─ garden
   │  │     ├─ member
   │  │     ├─ study
   │  │     └─ team
   │  │        ├─ api
   │  │        ├─ application
   │  │        │  └─ dto
   │  │        ├─ domain
   │  │        │  └─ repository
   │  │        └─ exception
   │  │           ├─ TeamException.java
   │  │           └─ TeamExceptionType.java
   │  └─ resources
   │     ├─ application.yml
   │     └─ schema.sql
   └─ test
```

## :hand: 역할 분담

#### 임연후(팜)
- **BackEnd**: 스터디 CRUD, 참여자 CRUD, S3 파일 저장, 학습자료 CRUD, RestDocs 세팅, Cors 설정

#### 송세연 (아마란스)
- **BackEnd**: 소셜 로그인, 팀원 목록 조회 및 검색
- **Infra**: 배포 서버 CICD 작성, 운영서버 Redis 세팅
  
#### 이수빈(짱구)
- **Infra**: CICD 파이프라인 구축, 백엔드 서버 도커 파일 작성, HTTPS 적용, CICD 캐싱 적용, CICD DB Docker 고도화, YML 서브모듈 도입, NGINX 설정

#### 이지민(미나)
- **BackEnd**: 커리큘럼 CRUD, 커리큘럼 관리

#### 손현경(보름)
- **BackEnd**: S3 이미지 저장, 팀원 초대 링크 생성, 팀원 추가
- **Infra**: CICD 파이프라인 구축, 백엔드 서버 도커 파일 작성

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


### 팀 랭킹
- **텃밭**: Github의 잔디를 모티브로 한 '텃밭'이 팀별로 주어지며, 이름 지표로 팀 랭킹이 매겨집니다.
- **협업**: 팀원들은 스터디, 학습자료 업로드 등의 팀 활동을 통해 팀의 '텃밭'에 기여할 수 있습니다.
- 1위부터 10위까지의 팀이 메인 페이지에 표시됩니다.
### 팀원
- **팀원 목록**: 해당 팀에 참여하고 있는 회원 목록을 제공합니다.
- **초대**: 팀장은 초대 버튼을 통해 팀에 새로운 회원을 초대할 수 있습니다.
- **출석률**: 오늘의 출석 인원 비율을 원 그래프로 확인할 수 있습니다.
### 팀 활동
- **스터디**: 팀에서 진행중인 스터디 목록과, 그 진행률을 확인할 수 있습니다.
- **학습자료**: 팀에 업로드된 학습자료 목록을 확인할 수 있습니다.
- ~**작물창고(데모 이후 개발)**: 팀에서 수집한 작물의 종류와 그 개수를 확인할 수 있습니다.~

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

### 스터디
- **참여자 목록**: 해당 스터디에 참여하고 있는 회원 목록을 제공합니다.
- **스터디 진행률**: 모든 팀원의 진척도를 합산하여 스터디 진행률을 계산합니다. 스터디 진행률에 따라 작물 일러스트가 성장합니다.
- **스터디 커리큘럼**: 개인별로 진행한 커리큘럼을 완료처리하거나, 커리큘럼의 순서를 바꿀 수 있습니다. 스터디장은 커리큘럼을 수정할 수 있습니다.
- **스터디 피드**: 모든 스터디 참여자의 진행상황을 피드에서 확인할 수 있습니다. 참여자가 커리큘럼을 완료하거나 학습자료를 업로드 할시 피드가 업데이트 됩니다.
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


### 학습자료
- **학습자료**: 스터디에서 공부한 내용을 학습자료 형태로 업로드 할 수 있습니다. 스터디에 업로드한 학습자료는 팀에도 공유됩니다.
  - 학습자료는 이미지, 파일, 링크의 형태로 업로드 가능합니다.
- ~북마크: 업로드된 학습자료를 북마크할 수 있습니다.~
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

### 마이페이지

- **텃밭**: 회원의 텃밭을 확인 가능합니다.
- **연속 출석일**: 회원의 연속 출석일을 확인 가능합니다.
- **나의 팀**: 소속된 팀을 아이콘 형태로 확인 가능합니다.
- **나의 스터디**: 소속된 스터디를 확인 가능합니다. 진행중인 스터디와 종료된 스터디를 나누어 확인할 수 있습니다. 

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

### 알림
- **알림**: 회원은 팀 내 스터디 개설, 팀원 초대, 직책 변경등의 소식을 알림으로 받을 수 있습니다.
  - 읽지 않은 알림과 읽은 알림을 분리하여 제공합니다. 
  - 알림을 선택하여 읽음 처리할 수 있습니다.   
<div align="center">
  <table>
  <tr>
    <td align="center">
      <strong>알림 페이지</strong>
    </td>    
  </tr>
  <tr>
    <td>
    <img width="600px" src="https://github.com/BDD-CLUB/01-doo-re-back/assets/71930280/4a4949fb-015d-47ca-97ef-3566b17a6b2f"/>
    </td>
  </tr>
  </table>
</div>
