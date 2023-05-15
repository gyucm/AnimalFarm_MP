# 미니 프로젝트 - SmartAnimalFarm

## 개요
![개요](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/6121b916-058d-4a5a-b4b3-aa579c2dd375)

- 농가의 인구가 지속적으로 하락하는 추세로 인력 부족에 대한 대책 필요
- 농가 소득 향상을 위한 IoT 시스템 구현 목표


## 구조
![123](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/39121e3b-44a7-4ecb-b414-651852f2d368)

- 아두이노 센서 값을 와이파이 통신을 통해 서버로 전송 
- STM은 값에 따라 모듈 작동, 라즈베리 파이는 Maria DB에 데이터 저장
- 안드로이드에서는 동작 제어 및 데이터 확인 가능


## 구현
- 안드로이드


![안드로이드](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/19e43221-c259-4ac0-8b11-c69de1f9faf2)

1. 모터가 작동되어 사료 공급 시 푸시 메세지
2. 버튼 터치에 따라 모듈 작동 및 상태 확인 가능


- Apache Server

![data2](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/dbb9cb9a-8a7c-4c79-96af-60ad23ae41e3)

1. 가축별 데이터 확인 가능
2. 온도, 습도 등 축사 상태 데이터 저장


- 아두이노

![아두이노](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/311d2f21-0c6b-4e8b-bc79-ed5d5334e46a)

1. 무게 센서를 통한 가축 무게 측정
2. 초음파 센서를 통해 사료통 사료 확인
3. 온습도 센서를 통한 축사 상태 체크
4. 와이파이 통신


- STM32

![STM](https://github.com/gyucm/AnimalFarm_MP/assets/123218906/48fa4f07-ca69-4ded-ac3e-40b5e46d5412)

1. 초음파 값에 따라 사료 서보 모터 작동
2. 온습도에 따라 LED, DC 모터 작동
3. 가축 상태 값 LCD 표기
