package pt.atp.cityalert.api

data class Ocorrencia(
        val id: Int,
        val descricao: String,
        val foto: String,
        val latitude: String,
        val longitude: String,
        val pessoa_id: Int,
        val tipo_id: Int
)
