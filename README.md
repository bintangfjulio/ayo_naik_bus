
# Ayo Naik Bus | Aplikasi Pemesanan Tiket Bus



<div align="center">
  <img align="center" src="https://github.com/bintangfjulio/bus_ticket_booking/blob/main/app/src/main/res/drawable/bus.png"></img>
  </div>


# Video Pengenalan Aplikasi
[![Sekarang di Android: 55] // Judul 
(https://github.com/bintangfjulio/bus_ticket_booking/blob/main/app/src/main/res/drawable/bus.png)] // Thumbnail 
(https://www.youtube.com/watch?v=u2j8k4-iXo4 "Sekarang di Android: 55") // Tautan Video

# Tentang Aplikasi

Aplikasi ini diperuntukkan untuk 2 role pengguna yaitu admin tiket bis dan pemesan tiket bis. Admin tiket bis dapat melakukan Create, Read, Update, and Delete tiket bis untuk dipesan dilengkapi dengan gambar bis dari tiket tersebut sedangkan pemesan tiket dapat melakukan Create & Read untuk pemesanan tiket bis yang sudah disediakan. Para pengguna dapat berdiskusi pada forum yang disediakan. Admin dan pemesan tiket harus melakukan login akun apabila ingin mengakses aplikasi yang mana pemesan tiket harus mendaftar apabila belum memiliki akun untuk login.

# Anggota Tim dan Pembagian Tugas


| Name                                    | Student-ID  |  Contacts                                                                                                                  |
| :-------------------------------------- | :---------- |  :------------------------------------------------------------------------------------------------------------------------ |
| Bintang Fajar Julio                           | 2007411033  |  [GitHub](https://github.com/bintangfjulio)                 |
| Muhammad Bintang Syawal                       | 2007411040  |  [GitHub](https://github.com/MBintangS)                     |
| Muhamad Dwiki Hermansyah                      | 2007411054  |  [GitHub](https://github.com/mdwikihermansyah)              |


# Tools

Aplikasi ini dibuat dengan menggunakan Tools [IDE Android Studio](https://developer.android.com/studio) dan menggunakan bahasa kotlin, serta menggunakan [Google Cloud Storage (Google Cloud Firebase Platform)](https://firebase.google.com/)
<br><br>
<div align="center">
  <img src="https://github.com/bintangfjulio/bus_ticket_booking/blob/dwiki/app/src/main/res/drawable/logo%20android%20studio.png" height="180" width="180"></img>
  <img src="https://github.com/bintangfjulio/bus_ticket_booking/blob/dwiki/app/src/main/res/drawable/logo%20firebase.png" height="180" width="180"></img>
</div>
  
# Fitur

Berikut adalah daftar fitur-fitur yang terdapat pada prototype ini

## Auth
- [x] Login
- [x] Register (sebagai admin/user)

## Admin
- [x] Search tiket bus yang sudah ditambahkan pada Home Page
- [x] Menampilkan list tiket
- [x] Menambahkan Tiket baru (Nama Bus, Jumlah Kursi, Harga Tiket, Tujuan, Tanggal Keberangkatan, Gambar Bus)
- [x] Menampilkan list tiket terbaru
- [x] Admin dapat menghapus, edit data tiket yang sudah tersedia
- [x] Chat dengan pengguna (broadcast)
- [x] Logout

      
## Pemesan
- [x] Search tiket bus yang sudah ditambahkan pada Home Page
- [x] Melihat list tiket yang tersedia
- [x] Mememsan tiket yang tersedia
- [x] Memasukkan data diri (Nama pemesan, Jumlah Kursi)
- [x] Melihat list pesanan tiket yang sudah ditambahkan
- [x] Chat dengan admin (broadcast)
- [x] Logout


# Apk File Download

APK prototype ini dapat di unduh pada link https://github.com/bintangfjulio/bus_ticket_booking/releases/tag/0.0.0 dari Release Repository ini

# Note
<div align="left">
  <p>* : Data tiket terdiri dari<p>&nbsp;&nbsp;• Nama Pemilik Bus</p><p>&nbsp;&nbsp;• Nama Pemesan</p><p>&nbsp;&nbsp;• Harga Tiket</p><p>&nbsp;&nbsp;• Jumlah Kursi/Tiket</p><p>&nbsp;&nbsp;• Tujuan</p><p>&nbsp;&nbsp;• Waktu Keberangkatan</p><p>&nbsp;&nbsp;• Harga Tiket</p></p>
  <p>** : Fitur login pada aplikasi ini menggunakan email*password, admin dan user memiliki domain email yang berbeda. Maka akan dialihkan ke halaman yang berbeda (sesuai dengan autorisasi pengguna) </p>
  </div>
<br><br><br><br><br><br>
<div align="right">
  <small>18 Juli 2023 - 03:25 README.md initialized by Muhamad Dwiki Hermansyah</small>  
</div>
