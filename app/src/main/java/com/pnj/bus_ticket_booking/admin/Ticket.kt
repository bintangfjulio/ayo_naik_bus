package com.pnj.bus_ticket_booking.admin

data class Ticket(
    var nama_bus: String?= null,
    var tanggal_keberangkatan: String?= null,
    var gambar_bus: String?= null,
    var jumlah_kursi: String?= null,
    var harga: String?= null,
    var tujuan: String?= null,
)
