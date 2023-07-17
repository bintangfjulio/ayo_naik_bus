package com.pnj.bus_ticket_booking.pemesan

data class Catalog(
    var nama_bus: String?= null,
    var tanggal_keberangkatan: String?= null,
    var gambar_bus: String?= null,
    var jumlah_kursi: String?= null,
    var harga: String?= null,
    var tujuan: String?= null,
)