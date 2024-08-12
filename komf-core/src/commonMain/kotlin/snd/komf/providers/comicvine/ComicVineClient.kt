package snd.komf.providers.comicvine

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import snd.komf.model.Image
import snd.komf.providers.comicvine.ComicVineClient.ComicVineTypeId.ISSUE
import snd.komf.providers.comicvine.ComicVineClient.ComicVineTypeId.VOLUME
import snd.komf.providers.comicvine.model.ComicVineIssue
import snd.komf.providers.comicvine.model.ComicVineIssueId
import snd.komf.providers.comicvine.model.ComicVineSearchResult
import snd.komf.providers.comicvine.model.ComicVineVolume
import snd.komf.providers.comicvine.model.ComicVineVolumeId
import snd.komf.providers.comicvine.model.ComicVineVolumeSearch

private const val baseUrl = "https://comicvine.gamespot.com/api/"

class ComicVineClient(
    private val ktor: HttpClient,
) {

    suspend fun searchVolume(name: String): ComicVineSearchResult<List<ComicVineVolumeSearch>> {
        return ktor.get("$baseUrl/search") {
            parameter("query", name)
            parameter("format", "json")
            parameter("resources", "volume")
        }.body()
    }

    suspend fun getVolume(id: ComicVineVolumeId): ComicVineSearchResult<ComicVineVolume> {
        return ktor.get("$baseUrl/volume/${VOLUME.id}-${id.id}") {
            parameter("format", "json")
        }.body()
    }

    suspend fun getIssue(id: ComicVineIssueId): ComicVineSearchResult<ComicVineIssue> {
        return ktor.get("$baseUrl/issue/${ISSUE.id}-${id.id}") {
            parameter("format", "json")
        }.body()
    }

    suspend fun getCover(url: String): Image {
        val bytes: ByteArray = ktor.get(url).body()
        return Image(bytes)
    }

    private enum class ComicVineTypeId(val id: Int) {
        VOLUME(4050),
        ISSUE(4000)
    }
}

