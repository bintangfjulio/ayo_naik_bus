package com.pnj.bus_ticket_booking.pemesan

data class Booking(
    var nama_pemilik: String?= null,
    var nama_tiket: String?= null,
    var waktu_keberangkatan: String?= null,
    var gambar_bus: String?= null,
)
