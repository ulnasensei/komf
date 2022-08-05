package org.snd.metadata.nautiljon

import org.snd.metadata.Provider
import org.snd.metadata.model.ProviderSeriesId
import org.snd.metadata.model.AuthorRole
import org.snd.metadata.model.SeriesMetadata
import org.snd.metadata.model.Thumbnail
import org.snd.metadata.model.BookMetadata
import org.snd.metadata.model.ProviderBookId
import org.snd.metadata.model.SeriesBook
import org.snd.metadata.nautiljon.model.Series
import org.snd.metadata.nautiljon.model.Volume

class NautiljonSeriesMetadataMapper(
    private val useOriginalPublisher: Boolean,
    private val originalPublisherTag: String?,
    private val frenchPublisherTag: String?
) {
    private val artistRoles = listOf(
        AuthorRole.PENCILLER,
        AuthorRole.INKER,
        AuthorRole.COLORIST,
        AuthorRole.LETTERER,
        AuthorRole.COVER
    )

    fun toSeriesMetadata(series: Series, thumbnail: Thumbnail? = null): SeriesMetadata {
        val status = when (series.status) {
            "En cours" -> SeriesMetadata.Status.ONGOING
            "En attente" -> SeriesMetadata.Status.ONGOING
            "Terminé" -> SeriesMetadata.Status.ENDED
            else -> SeriesMetadata.Status.ONGOING
        }


        val authors = series.authorsStory.map { org.snd.metadata.model.Author(it, AuthorRole.WRITER.name) } +
                series.authorsArt.flatMap { artist -> artistRoles.map { role -> org.snd.metadata.model.Author(artist, role.name) } }

        val tags = series.themes + listOfNotNull(
            originalPublisherTag?.let { tag ->
                series.originalPublisher?.let { publisher -> "$tag: $publisher" }
            },
            frenchPublisherTag?.let { tag ->
                series.frenchPublisher?.let { publisher -> "$tag: $publisher" }
            }
        )

        return SeriesMetadata(
            id = ProviderSeriesId(series.id.id),
            provider = Provider.NAUTILJON,

            status = status,
            title = series.title,
            titleSort = series.title,
            summary = series.description ?: "",
            publisher = (if (useOriginalPublisher) series.originalPublisher else series.frenchPublisher) ?: "",
            genres = series.genres,
            tags = tags,
            authors = authors,
            thumbnail = thumbnail,
            totalBookCount = series.numberOfVolumes,
            ageRating = series.recommendedAge,

            books = series.volumes.map {
                SeriesBook(
                    id = ProviderBookId(it.id.id),
                    number = it.number,
                    edition = it.edition,
                    type = it.type,
                    name = it.name
                )
            }
        )
    }

    fun toBookMetadata(volume: Volume, thumbnail: Thumbnail? = null): BookMetadata {
        val authors = volume.authorsStory.map { org.snd.metadata.model.Author(it, AuthorRole.WRITER.name) } +
                volume.authorsArt.flatMap { artist -> artistRoles.map { role -> org.snd.metadata.model.Author(artist, role.name) } }

        return BookMetadata(
            summary = volume.description ?: "",
            number = volume.number,
            releaseDate = if (useOriginalPublisher) volume.originalReleaseDate else volume.frenchReleaseDate,
            authors = authors,
            startChapter = null,
            endChapter = null,

            thumbnail = thumbnail
        )
    }
}
