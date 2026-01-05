```markdown
# KassApp - Aplikasi Pencatatan Transaksi Penjualan

## 📱 Tentang Aplikasi

**KassApp** adalah aplikasi berbasis Android yang dikembangkan secara khusus untuk membantu optimalisasi manajemen operasional pada Warung Makan Indomie Raja Vitamin 3. Fokus utama pengembangan ini adalah digitalisasi pencatatan transaksi penjualan yang sebelumnya masih bersifat konvensional.

### Latar Belakang

Sebelum implementasi aplikasi, proses pencatatan transaksi (baik tunai maupun QRIS) dilakukan secara manual menggunakan media buku yang terpisah. Hal tersebut menimbulkan beberapa hambatan teknis, di antaranya:
- Rendahnya efisiensi waktu
- Risiko kesalahan manusia (human error) dalam penghitungan
- Potensi kehilangan data transaksi

### Fitur Utama

- **🔐 Autentikasi Keamanan**: Login terintegrasi menggunakan akun Google
- **📋 Manajemen Inventori**: Pengelolaan data menu makanan dan minuman secara sistematis
- **💰 Sistem Transaksi Digital**: Pencatatan metode pembayaran tunai dan QRIS dengan kalkulasi total otomatis
- **☁️ Keamanan Data**: Fitur backup dan restore data melalui Google Drive untuk menjamin keberlangsungan data penjualan

## Tim Pengembang

1. Abrar Imam Satria – 20220140042
2. Muhajirah Ulfah – 20220140093
3. Latif Usmul Fauzi – 20220140181
4. Sal Sabila – 20220140189


## 🚀 Cara Menjalankan Aplikasi

### Metode 1: Instalasi APK (Untuk Pengguna Akhir)

Metode ini direkomendasikan untuk pengguna akhir karena tidak memerlukan konfigurasi teknis tambahan:

1. Unduh dan pasang file APK KassApp pada perangkat Android
2. Buka aplikasi yang telah terpasang
3. Masuk (Login) menggunakan akun Google
4. Aplikasi siap digunakan untuk mencatat transaksi

### Metode 2: Android Studio (Untuk Pengembang)

Metode ini digunakan untuk keperluan pengujian kode atau pengembangan lebih lanjut.

#### 1. Persiapan Project

- Buka Android Studio dan pilih menu **Open Project**
- Arahkan pada direktori folder source code KassApp
- Lakukan proses **Gradle Sync** dan tunggu hingga seluruh dependensi terintegrasi
- Siapkan emulator Android atau perangkat fisik melalui mode USB Debugging

#### 2. Konfigurasi Google Cloud Console

Integrasi ini diperlukan agar fitur Google Sign-In dan Google Drive API dapat berjalan pada lingkungan pengembangan lokal:

**a. Pembuatan Project**
- Akses [Google Cloud Console](https://console.cloud.google.com/)
- Buat project baru

**b. Aktivasi API**
- Aktifkan layanan **Google Drive API**

**c. Konfigurasi OAuth**
- Buka menu **APIs & Services > Credentials**
- Pilih **Create Credentials > OAuth Client ID** dengan tipe aplikasi **Android**
- Input **Package Name**: `com.example.kasapp`
- Input nilai **SHA-1** (didapatkan melalui langkah berikutnya)

#### 3. Prosedur Pengambilan SHA-1

- Buka **Terminal** di dalam Android Studio
- Jalankan perintah:
  - **Windows**: 
    ```bash
    gradlew signingReport
    ```
  - **macOS/Linux**: 
    ```bash
    ./gradlew signingReport
    ```
- Salin kode pada bagian **SHA1** di bawah keterangan `Variant: debug`
- Tempelkan kode tersebut pada kolom konfigurasi di Google Cloud Console

#### 4. Eksekusi Aplikasi

- Tekan tombol **Run** (Ikon Play) pada Android Studio
- Lakukan login akun Google untuk memverifikasi sinkronisasi

## 🛠️ Spesifikasi Teknologi

| Komponen | Spesifikasi |
|----------|-------------|
| Bahasa Pemrograman | Kotlin |
| Versi Kotlin | 1.9.10 |
| Platform | Android |
| Arsitektur UI | Jetpack Compose (Declarative UI) |
| Penyimpanan Cloud | Google Drive API (Integration) |
| Autentikasi | Google Sign-In SDK |

## 📄 Lisensi
Project ini dikembangkan oleh kelompok Capstone sebagai bagian dari mata kuliah Capstone 2 Tahun Ajaran 2025/2026 dan ditujukan untuk mendukung operasional Warung Makan Indomie Raja Vitamin 3.

Seluruh source code dan aset yang terdapat dalam project ini digunakan untuk keperluan akademik dan evaluasi pembelajaran.  
Penggunaan, penggandaan, dan distribusi project ini untuk tujuan komersial tidak diperkenankan tanpa izin dari pengembang.

---

**Catatan**: Pastikan perangkat Android yang digunakan memiliki koneksi internet untuk dapat menggunakan fitur autentikasi Google dan sinkronisasi data dengan Google Drive.
```