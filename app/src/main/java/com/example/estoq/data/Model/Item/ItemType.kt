package com.example.estoq.data.Model.Item

enum class ItemType(val code: String, val displayName: String) {
    BLUSA("BL", "Blusa"),
    REGATA( "RE", "Regata"),
    CALCA("CA", "Calça"),
    VESTIDO("VE", "Vestido"),
    JAQUETA("JA", "Jaqueta"),
    CALCA_ALFAIATARIA("CAl", "Calça alfaiataria"),
    SHORTS("SH", "Short"),
    SHORT_ALFAIATARIA("SAl", "Short em alfaiataria"),
    SHORT_JEANS("SJ", "Short jeans"),
    CALCA_JEANS("CJ", "Calça jeans"),
    BOLSAS("BO", "Bolsa"),
    SAIAS("SA", "Saia"),
    SHORT_SAIA("SS", "Short saía");

    fun availableSizes(): List<String> = when (this) {
        CALCA, CALCA_ALFAIATARIA, SHORTS, SHORT_ALFAIATARIA, SHORT_JEANS, CALCA_JEANS, SAIAS, SHORT_SAIA ->
            (34..48 step 2).map { it.toString() }
        BOLSAS -> listOf("Único")
        else -> listOf("PP", "P", "M", "G", "GG")
    }

    fun availableColors(): List<String> = when (this) {
        BLUSA, REGATA, CALCA, VESTIDO, JAQUETA, CALCA_ALFAIATARIA, SHORTS, SHORT_ALFAIATARIA, SAIAS, SHORT_SAIA, BOLSAS ->
            listOf("Azul", "Vermelho", "Vinho", "Verde", "Amarelo", "Preto", "Branco", "Cinza", "Laranja", "Rosa Pink", "Rosa Claro", "Roxo", "Marrom", "Bege", "Dourado", "Prateado")
        SHORT_JEANS, CALCA_JEANS -> listOf("Jeans Azul", "Jeans Preto", "Jeans Cinza", "Jeans Branco", "Jeans Verde", "Jeans Vermelho")
    }
}
