package com.example.estoq.data.Model.Item

enum class ItemType(val code: String, val displayName: String) {
    BLUSA("BL", "Blusa"),
    CALCA("CA", "Calça"),
    VESTIDO("VE", "Vestido"),
    JAQUETA("JA", "Jaqueta"),
    CALCA_ALFAIATARIA("CAl", "Calça alfaiataria"),
    SHORTS("SH", "Shorts"),
    SHORT_ALFAIATARIA("SAl", "Short em alfaiataria"),
    SHORT_JEANS("SJ", "Short jeans"),
    CALCA_JEANS("CJ", "Calça jeans"),
    BOLSAS("BO", "Bolsas"),
    SAIAS("SA", "Saias"),
    SHORT_SAIA("SS", "Short saía");

    fun availableSizes(): List<String> = when (this) {
        CALCA, CALCA_ALFAIATARIA, SHORTS, SHORT_ALFAIATARIA, SHORT_JEANS, CALCA_JEANS, SAIAS, SHORT_SAIA ->
            (34..48 step 2).map { it.toString() }
        BOLSAS -> listOf("Único")
        else -> listOf("PP", "P", "M", "G", "GG")
    }
}
