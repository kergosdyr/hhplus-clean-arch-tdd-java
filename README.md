## 요구사항 정리

### **특강 신청 API**

**기능 설명:**

- 특정 사용자(`userId`)가 특강을 선착순으로 신청합니다.

**요구사항:**

1. **중복 신청 방지:**
    - 동일한 `userId`는 동일한 `Lecture`에 대해 한 번만 신청 가능합니다.
    - 동일 사용자의 중복 신청 요청은 실패해야 합니다.
2. **정원 제한:**
    - 각 특강(`Lecture`)은 **정원이 30명**으로 고정됩니다.
    - 이미 신청자가 **30명 이상인** 경우, 이후 신청 요청은 실패해야 합니다.
    - 특강 일자(`lectureDate`) 가 신청 일자와 같거나 과거인 경우에 요청은 신청 요청은 실패해야합니다.
3. **신청 성공 조건:**
    - 정원이 30명 이내이고 특강 일자가 현재 신청일자와 같거나 과거가 아닌 경우 `userId`의 신청이 성공적으로 처리됩니다.

---

### **특강 선택 API**

**기능 설명:**

- 날짜별로 현재 신청 가능한 특강 목록을 제공합니다.

**요구사항:**

1. **날짜별 조회:**
    - 주어진 날짜(`lectureDate`)에 신청 가능한 특강 목록을 반환합니다.
2. **신청 가능 여부:**
    - **30명 정원** 중 **잔여 인원**이 있는 특강만 조회됩니다.
3. **반환 데이터:**
    - 특강 ID (`lectureId`), 제목(`title`), 강연자 정보(`presenter`)를 포함해야 합니다.
    - 반환데이터는 List 형식으로 표현되며, 다수의 데이터를 가질 수 있습니다.

---

### **특강 신청 완료 목록 조회 API**

**기능 설명:**

- 특정 사용자(`userId`)가 신청 완료한 특강 목록을 조회합니다.

**요구사항:**

1. **신청 완료 데이터 조회:**
    - 사용자가 신청 완료한 모든 특강(`Lecture`) 목록을 반환합니다.
2. **반환 데이터 구성:**
    - 특강 ID (`lectureId`), 제목(`title`), 강연자 정보(`presenter`)를 포함해야 합니다.

## ERD 설계

![image](https://github.com/user-attachments/assets/ce656194-5258-44ec-925e-93f5cf5d8ef1)

### 공통 필드

- `createdAt` : 해당 Row 의 저장 시간을 저장합니다.
- `updatedAt` : 해당 Row 의 가장 최근 업데이트 시간을 저장합니다.

### **Lecture 테이블**

- **역할: 특강 정보 관리**
    - 각 특강의 기본 정보(제목, 날짜, 정원 관리 등)를 저장합니다.
    - 선착순 제한과 관련된 신청자를 관리하기 위한 기준 데이터가 됩니다.
- **주요 필드:**
    - `id`: 각 특강을 고유하게 식별하기 위한 기본 키.
    - `presenterId`: 특강을 진행하는 강연자와 연결하기 위한 일종의 외래 키(**실제 외래키를 사용하지는 않음**)
    - `attendeeCount`: 현재 특강에 신청한 인원의 수를 실시간으로 관리.
    - `lectureDate`: 신청 가능한 날짜별 목록 조회를 위한 날짜 정보.
- **요구사항 대응:**
    - `attendeeCount`를 활용해 실시간으로 정원을 관리하며, 선착순 인원수 제한을 구현할 수 있습니다.
    - `lectureDate` 필드를 기준으로 날짜별 신청 가능한 특강 목록을 조회할 수 있습니다.

---

### **LectureRegister 테이블**

- **역할: 신청 데이터 관리**
    - 각 사용자의 특강 신청 내역을 저장합니다.
    - 중복 신청 방지와 신청 완료 목록 조회 기능의 핵심 데이터입니다.
- **주요 필드:**
    - `id`: 각 신청 내역을 고유하게 식별하기 위한 기본 키.
    - `lecture_id`: 신청된 특강과 연결하기 위한 일종의 외래 키(**실제 외래키를 사용하지는 않음**)
    - `user_id`: 신청한 사용자를 식별하기 위한 사용자 ID.
    - `registeredAt`: 신청 시간 정보로, 기록 관리등을 위함
- **요구사항 대응:**
    - `user_id`와 `lecture_id`의 조합을 고유(unique) 제약 조건으로 설정해 중복 신청을 방지합니다.
    - `lecture_id`를 기준으로 신청 데이터를 집계해 특강별 신청자 수를 파악할 수 있습니다.

---

### **Presenter 테이블**

- **역할: 강연자 정보 관리**
    - 특강의 강연자 정보를 저장합니다.
    - 강연자 정보를 특강과 연결해 반환 데이터에 포함합니다.
- **주요 필드:**
    - `id`: 각 강연자를 고유하게 식별하기 위한 기본 키.
    - `name`: 강연자의 이름.
    - `email`: 강연자의 이메일 주소로, 신청 완료 목록 조회 시 사용.
- **요구사항 대응:**
    - `Lecture` 테이블의 `presenterId`와 연결해 강연자 정보를 제공합니다.
    - 신청 완료 목록 조회 시 강연자 정보를 반환하기 위해 필요한 데이터를 포함합니다.

---

### **ERD의 설계 이유**

1. **Lecture와 LectureRegister의 분리**:
    - 특강 정보(`Lecture`)와 신청 정보(`LectureRegister`)를 분리함으로써 강의 자체의 데이터와 신청자의 데이터를 독립적으로 관리할 수 있습니다.
    - 이를 통해 정원 관리와 중복 신청 방지가 효율적으로 이루어집니다.
    - **선착순 정원 관리**
        - `attendeeCount` 필드를 `Lecture` 테이블에 포함시켜 실시간으로 신청 현황을 관리할 수 있도록 설계했습니다.
        - `LectureRegister` 테이블에서 `lecture_id`를 기반으로 집계하면 정원을 파악할 수 있지만, 중복 집계 오버헤드를 줄이기 위해 별도의 카운트 필드를 사용합니다.
        - **`attendeeCount` 필드를 사용하여 `db lock` 을 제어합니다. 낙관적 락, 비관적 락 모두 DB Row 기반이므로 해당 필드를 이용하여 효율적으로 `db lock` 을 제어할 수 있습니다.**
2. **Presenter와 Lecture의 연결**:
    - `Presenter`  정보는 여러 특강에서 재사용될 수 있으므로 독립적인 테이블로 정규화하여 관리합니다.
    - 이를 통해 중복 데이터 입력을 방지하고, `Presenter`  정보 업데이트 시 여러 특강에 영향을 줄 수 있습니다.
3. **중복 신청 방지**:
    - `LectureRegister` 테이블에서 `user_id`와 `lecture_id`의 조합에 고유 제약 조건을 설정하여 중복 신청을 방지합니다.
4. **`User` 테이블의 부존재**
    - 요구사항에 `User` 에 관한 구현은 생략하도록 기재되어있으므로 해당 테이블은 생략하였습니다. 만약 필요하다면 `LectureRegister` 의 `user_id` 와 연결하여 구성합니다.
5. **외래키(FK) 미사용**
    - FK의 사용은 최대한 지양하였습니다. 휴리스틱한 접근이지만 실무에서의 경험으로 FK 가 있을때의 문제가 없을떄의 문제보다 조금 더 강하게 다가왔기 때문에 연결하는 것은 지양하였습니다.
    - 대신 일종의 논리적 FK로 LectureId, PresenterId 를 필드로 포함하도록 구성하여 조인하여 조회할 수 있도록 하였습니다.


## 아키텍처 설계를 위한 패키지 설계 전략

```java
src/
└── main/
    └── java/
        └── com/
            └── justin/
                └── clean/
                    ├── app/          # 애플리케이션(Usecase, facade / Business)
                    ├── domain/       # 도메인 모델(Domain / Business)
                    ├── enums/        # 열거형 정의(*횡단 관심사)
                    ├── error/        # 에러 관리(*횡단 관심사)
                    ├── storage/      # 저장소 계층(JPA, Datasource / Infrastructure)
                    ├── web/          # 웹 계층 (API 엔드포인트 / Presentation)
                    └── WebApiApplication # 애플리케이션 엔트리포인트
```

![image](https://github.com/user-attachments/assets/08356523-8ea7-4976-920c-77c6837975e1)


### 패키지 설계 전략

1. `app` : 어플리케이션 패키지로 **Business 레이어에 해당하며**  `usecase` 혹은 `facade`, `service` 라고 불리는 비즈니스 서비스 로직들을 처리합니다.
    - `usecase` 혹은 `facade`, `service` 을 모두 표현하기 위한 단어는 `application`이라고 판단하였으며 축약형 단어지만 일반적으로 통용되는 단어인 `app` 을 사용하였습니다.
    - `domain` 패키지와 함께 Business 영역을 담당하고, 비즈니스 로직등을 구현하기 위한 도구들을 응집시킵니다.
    - `domain` 패키지의 코드들과 강한 결합을 가지지만, 의존성의 방향은 `app` → `domain` 으로만 흐르게 구성합니다.
2. `domain` : 도메인 패키지로 **Business 레이어에 해당하며** 핵심 비즈니스 로직의 개념적인 요소를 표현합니다. 예를 들면 Lecture 와 같이 한개의 개념적인 요소로 표현할 수 있는 코드를 응집합니다.
    - JPA `@Entity` 를 사용하여 표현하는 요소들을 응집시킵니다.
    - `app` 과 분리한 이유는 `@Entity` 간의 연관관계가 있을 경우에 빠르게 확인할 수 있도록 하고 `domain` 에서는 다른 계층으로의 의존을 허용하지 않기 때문에 import 문으로 빠르게 확인할 수 있도록 하기 위함입니다.
3. `storage` : 스토리지 패키지로 **Infrastructure 레이어에 해당하며** DB 에 가장 직접적으로 결합되어있는 패키지입니다. `app` 의 인터페이스를 `JPA` 와 같은 기술을 사용하여 직접 구현합니다.
    - `domain` 과 강하게 결합되어있지만 의존성의 방향은 `stroage` → `domain` 으로 한방향으로 흐르도록 구성합니다.
    - `app` 의 Repository 인터페이스를 실제로 구현합니다. 해당 패키지에서 사용하는 기술이 JPA 에서 Mybatis 혹은 다른 방식으로 바뀌더라도 `app` 의 구현은 변경되지 않습니다.
4. `web` : 웹 패키지로 **Presentation 레이어에 해당**하며 Controller 와 요청, 응답 DTO 등이 포함됩니다.
    - Restful API 요청을 처리하고 데이터의 흐름을 제어하며 데이터 정합성의 필터 역할을 합니다.
    - `app` , `domain` 영역과 강하게 결합되어있지만, 의존성의 방향은 `web` → `app`, `domain` 한 방향으로 흐르도록 구성되어있습니다.
    - `stroage` 와 같은 infrastructure 레이어에 직접적으로 의존하지 않습니다.

## 동시성 제어 관련 요구사항 분석 및 구현

### 동시성 제어 관련 요구사항

- 특강 신청 및 신청자 목록 관리를 RDBMS를 이용해 관리할 방법을 고민합니다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
- 동시성 이슈를 고려 하여 구현합니다.

### DB 에서의 동시성 제어

주어진 요구사항을 분석해보자면, 다수의 인스턴스에서 하나의 DB 를 바라보는 상황에서의 동시성 제어를 구현해야합니다. 

즉, 인스턴스 레벨에서의 동시성 제어(`semaphore`, `syncrhonized`) 등이 아닌 DB 에서의 동시성 제어가 필요합니다.

일반적으로 DB에서의 동시성 제어는 비관적 락, 낙관적 락으로 구현합니다.

### **비관적 락(Pessimistic Lock)**

### **1. 개념**

- **비관적 락**은 데이터의 동시 수정 가능성을 부정적으로(비관적으로) 판단하고, 데이터에 접근하는 동안 **다른 트랜잭션이 해당 데이터를 수정하지 못하도록 차단**합니다.
- 주로 **트랜잭션 충돌 가능성이 높은 경우**에 사용됩니다.

### **2. 특징**

- 데이터를 조회하거나 수정하려고 할 때, 해당 데이터에 락을 걸어 다른 트랜잭션이 접근하지 못하도록 합니다.
- 락을 걸기 때문에 **데드락(Deadlock)** 가능성이 존재하며, **성능 저하**를 초래할 수 있습니다.
- **즉시 동시성 문제를 차단**할 수 있으므로 데이터 정합성 보장이 확실합니다.

### **3. 구현 방법**

- SQL의 **`SELECT ... FOR UPDATE`** 문을 사용하여 행 단위로 락을 걸 수 있습니다.

### **4. 장점**

- 트랜잭션 충돌을 미리 방지하여 **데이터 정합성을 확실히 보장**합니다.

### **5. 단점**

- 락을 거는 동안 다른 트랜잭션이 대기해야 하므로 **성능 저하**가 발생할 수 있습니다.
- 데이터에 락이 오래 걸리면 **데드락** 발생 가능성이 높아집니다.

---

### **낙관적 락(Optimistic Lock)**

### **1. 개념**

- **낙관적 락**은 데이터 충돌 가능성을 낮게(낙관적으로) 판단하여, 트랜잭션 간의 충돌을 허용한 뒤, **최종 커밋 단계에서 충돌 여부를 검증**합니다.
- 주로 **트랜잭션 충돌 가능성이 낮은 경우**에 사용됩니다.

### **2. 특징**

- 데이터에 락을 걸지 않고, 트랜잭션이 끝날 때 데이터가 변경되었는지 확인합니다.
- 일반적으로 **버전 관리**를 통해 데이터 충돌 여부를 확인합니다.

### **3. 구현 방법**

- 데이터베이스 테이블에 `version` 또는 `timestamp` 필드를 추가하여 사용합니다.
- 엔티티를 수정할 때, `version` 필드 값을 비교하여 데이터가 다른 트랜잭션에서 수정되지 않았는지 확인합니다.
- 예제 (JPA):
    
    ```java
    @Version
    private int version;
    
    public void updateLecture(String title) {
        this.title = title;
    }
    
    ```
    
    - 엔티티를 업데이트하려고 할 때 `version` 값을 자동으로 검증합니다.

### **4. 장점**

- 데이터에 락을 걸지 않으므로 **성능**이 좋습니다.
- 트랜잭션 충돌이 드물 경우 효율적입니다.

### **5. 단점**

- 트랜잭션 충돌이 발생하면 **재시도 로직**을 추가로 구현해야 하며, 복잡성이 증가할 수 있습니다.
- 데이터 충돌 시 **낮은 신뢰성** 문제를 발생시킬 수 있습니다.

### 어떻게 동시성 제어를 구현했는가?

분석한 동시성 제어 방식을 토대로 비관적 락, 낙관적 락을 구현해보기로 했습니다. 더 좋은 방식에 대한 판단을 내리기 위해 두가지 락 모두 성능을 테스트해보았습니다.

### 낙관적 락의 구현, 성능 측정

- 낙관적 락을 구현하기 위해서 `Lecture` 테이블에 `version` 컬럼을 추가하고, JPA 의 `@Version` 을 사용하여 낙관적 락을 구현하였습니다.
- 그리고 최종적 정합성을 맞추기 위해 `spring-retry` 라이브러리를 적용하고 `@Retryable` 어노테이션을 사용해서 `OptimisticLockException` 등이 발생한 경우 재시도 하도록 구성했습니다.
- 최대 재시도 횟수(`maxAttempt`) , 재시도 시간(`backoff`) 등의 수치도 성능상의 영향에 영향을 미치기 때문에 우선 최적의 수치를 찾는것 부터 시작하였습니다.
- 테스트는 주어진 요구사항 테스트 코드인 `동시에 동일한 특강에 대해 40명이 신청했을 때 30명만 성공한다` 를 `@ReaptedTest(30)` 을 사용하여 30번 재시도하고 그 시간의 총합을 구하기로 하였습니다.

1. `maxAttempt`: 3, `backoff` : 1000(ms)

![image](https://github.com/user-attachments/assets/f2d3bfd5-985b-4951-b6e6-15805f04949e)

1. `maxAttempt`: 5, `backoff` : 100(ms)

![image](https://github.com/user-attachments/assets/29635ffe-b8aa-49e4-8553-f7fc9b288c46)

1. `maxAttempt`: 3, `backoff` : 200(ms) (`multiply` 1.5) (GPT o1 추천)

![image](https://github.com/user-attachments/assets/6f4ff5f3-14d8-4817-a599-3c306fbe02d8)

1. `maxAttempt`: 5, `backoff` : 50(ms)(`multiply` 1.5)

![image](https://github.com/user-attachments/assets/a1a4e1a0-2578-4c6e-90ae-29c95865c6d0)

1. `maxAttempt`: 6, `backoff` : 30(ms)(`multiply` 1.2)

![image](https://github.com/user-attachments/assets/a30fb30c-0724-43ab-853f-cd075454a1d3)

### 테스트 결과

- 최초에 `backoff` 가 1000(ms) 로 매우 큰 경우에는 트랜잭션 타임아웃이나 테스트용 thread 의 타임아웃으로 인해 테스트가 실패하는 경우가 많았습니다.
- OpenAI o1 의 추천으로 구성한 값의 경우 `multiply` 즉, 시도마다 조금 더 긴 시간을 기다리도록 하는 설정을 추가하였습니다.
- 많은 테스트를 해서 최적화 한것은 아니지만, 몇가지 추천 받은 값을 사용해보았을때 `maxAttempt` 가 5 이고 `backoff` 가 30(ms)이고 `multiply` 1.2 인 경우에 성능이 가장 좋았으며. 총 30회에 약 6초 정도의 시간이 걸렸습니다.
    - 물론, 더 좋은 성능인 값도 있겠지만 성능만이 목표는 아니라서 이정도의 실험으로 마무리 하였습니다.

### 비관적 락 의 구현, 성능 측정

- 서칭 결과, 낙관적 락의 성능이 `일반적으로` 더 좋다고 나와있었기 때문에 비관적 락의 성능에 대해서 크게 기대하지 않았습니다.
- JPA 의 `@Lock` 을 사용하여 비관적 락을 사용하였으며. Lecture 테이블의 Row 를 `FOR UPDATE` 구문을 통해 구현했습니다.
- 낙관적 락 테스트와 동일한 조건에서 테스트를 진행하였습니다.

- `@Lock`

![image](https://github.com/user-attachments/assets/7ecf28a3-0e0c-4af2-bc3c-6c5580c46afe)

### 테스트 결과

- 예상한것과는 너무도 다른 결과가 나타나서 당황스러웠습니다. 일반적으로 서칭했을 때 낙관적 락이 훨씬 더 성능이 좋다고 했지만 비관적 락이 몇번을 테스트 해도 **누적 테스트 시간이 2-3초** 정도였습니다.
- 낙관적 락이 길면 1분, 짧았을때도 6초 정도가 걸렸던것에 비하면 비관적 락이 훨씬 더 좋은 성능을 보이고 있었습니다.
- 그리고 저는 머리가 아프기 시작했습니다.
  
### 테스트 결과 분석과 요구사항 구현

왜 비관적 락이 더 빨랐을까요? 저는 제가 알고있던 상식과 예상을 벗어나는 결과에 당황했습니다. 구현을 잘못한 것인가? 코드를 살펴보았지만 잘못된 곳은 없었습니다.

왜 비관적 락이 더 빠른지에 대해 분석하기 위해 다시 자료를 살펴보았습니다. 그 중에 눈에 들어온것은 낙관적 락의 기본조건 `트랜잭션 충돌 확률이 낮은 경우 사용합니다` 와 비관적 락의 기본조건인 `주로 트랜잭션 충돌 가능성이 높은 경우에 사용` 이 눈에 띄었습니다.

우리의 테스트는 40명이 동시에 특강을 신청해서 모두가 충돌하는 `트랜잭션 충돌 확률이 매우 높은 테스트`였습니다. 그리고 요구사항 역시 특강 신청과 같이 특정 시점에 다수가 한꺼번에 충돌할 확률이 높은 상황을 상정하고 있었습니다.

낙관적 락은 충돌이 잘 발생하지 않는 일반적인 상황에서의 성능은 보장 되지만, 특강 신청과 같은 비즈니스영역에서는 좋지 않은 선택이었습니다.

그래서 저는 주어진 요구사항 즉, 해당 비즈니스 영역에서 조금 더 좋은 선택은 **비관적 락**을 통해서 구현하는 것이라는 결론을 내리고, 요구사항을 비관적 락을 통해서 구현하였습니다.

하지만, 데드락 가능성 및 일반적 상황에서의 성능은 낙관적 락이 훨씬 더 나은 선택이기 때문에 현재 주어진 특수한 상황을 제외한 일반적인 상황에서는 **낙관적 락** 을 사용해서 구현하는것이 좀더 좋은 선택이라고 생각합니다.
