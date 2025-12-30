package com.example.khedmabackend.model;

// ✅ Nouveau : Enum BookingStatus (fichier séparé pour public enum)
public enum BookingStatus {
    PENDING, // "En cours"
    IN_PROGRESS,
    CONFIRMED, // "Confirmée"
    REJECTED // "Annulée"
}