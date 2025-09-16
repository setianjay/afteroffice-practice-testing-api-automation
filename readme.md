# 🔍 API Automation Testing Project

Project ini dibuat untuk melakukan **otomatisasi pengujian API** menggunakan Java.  
Tujuan utama dari project ini adalah untuk memastikan API berjalan sesuai dengan requirement yang sudah ditentukan.
---

## 📖 Daftar Isi
- [Tech Stack](#-tech-stack)
- [Struktur Project](#-struktur-project)
- [API Endpoint](#-apiendpoint)
- [Instalasi](#-instalasi)
- [Menjalankan Test](#-menjalankan-test)
- [Support Me](#-support-me)
- [Dependencies](#dependencies)

---

## 🛠 Tech Stack
- **Bahasa**: Java 21
- **Build Tool**: Maven
- **Testing Framework**: TestNG
- **Library API Testing**: RestAssured

---

## 📂 Struktur Project
```bash
api-automation/
├── src/
│   └── test/java/com/setianjay/
│       ├── base/
│       │   └── BaseTest.java
│       │
│       ├── constants/
│       │   ├── FileConstant.java
│       │   └── NetworkConstant.java
│       │
│       ├── enums/
│       │   └── Method.java
│       │
│       ├── models/
│       │   ├── request/
│       │   │   └── booking/
│       │   │       └── BookingAuthRequest.java
│       │   │
│       │   └── response/
│       │       ├── booking/
│       │       │   ├── BookingAuthResponse.java
│       │       │   ├── BookingCreateResponse.java
│       │       │   ├── BookingDatesResponse.java
│       │       │   ├── BookingIdResponse.java
│       │       │   └── BookingResponse.java
│       │       │
│       │       └── phone/
│       │           ├── PhoneResponse.java
│       │           └── PhoneSpecificationResponse.java
│       │
│       ├── tests/
│       │   ├── BookingApiTest.java
│       │   └── PhoneApiTest.java
│       │
│       └── utils/
│           ├── AnnotationUtil.java
│           ├── JsonUtils.java
│           └── LoggerUtils.java
│
├── testng.xml
├── pom.xml
└── README.md
```

---


## 🌐 API Endpoint
Project ini menggunakan beberapa endpoint publik untuk keperluan testing:

1. [Restful Booker](https://restful-booker.herokuapp.com)  
   Digunakan untuk menguji fitur **Booking API** (create, update, delete, get booking, dll).
   ### Restful Booker
   - `POST /auth` → Membuat token
   - `POST /booking` → Membuat booking baru
   - `GET /booking/{id}` → Mengambil data booking berdasarkan ID
   - `PUT /booking/{id}` → Update booking
   - `DELETE /booking/{id}` → Hapus booking

2. [Restful API](https://restful-api.dev/)  
   Digunakan untuk menguji fitur **Phone API** (get phone list, get phone by id, dll).
    ### Restful API
   - `GET /objects` → Mendapatkan list semua objek (misal data handphone)
   - `GET /objects/{id}` → Mendapatkan detail objek berdasarkan ID
---

## ⚙️ Instalasi

1. Clone repository
   ```bash
   git clone https://github.com/username/api-automation.git
   cd api-automation
   ```

2. Install dependencies
   ```bash
   mvn clean install
   ```

---

## ▶️ Menjalankan Test

### Jalankan semua test
```bash
mvn test
```

### Jalankan test berdasarkan suite `testng.xml`
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

---

## 🧹Dependencies
- [Rest Assured](https://rest-assured.io/)
- [TestNg](https://testng.org)
- [Jackson](https://github.com/FasterXML/jackson)
- [Lombok](https://projectlombok.org/)
- [Slf4j](https://www.slf4j.org/)
- [Logback](https://logback.qos.ch/)
---

## 🤝 Support Me
Just **Give Star** for this repository or **Follow** my Github, you have **Supported Me**.

---

## 🧔 Author
Hari Setiaji - [setianjay](https://github.com/setianjay) on Github, [Hari Setiaji](https://www.linkedin.com/in/hari-setiaji-3412ba189/) on Linkedin.
