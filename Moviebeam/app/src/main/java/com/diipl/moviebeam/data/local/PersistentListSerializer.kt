
package com.diipl.moviebeam.data.local

/*
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class ContentDtoPersistentListSerializer(
    private val serializer: KSerializer<ContentDto>,
) : KSerializer<PersistentList<ContentDto>> {

    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<ContentDto>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<ContentDto>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<ContentDto> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class PremiumGenreContentDtoPersistentListSerializer(
    private val serializer: KSerializer<PremiumGenre>,
) : KSerializer<PersistentList<PremiumGenre>> {

    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<PremiumGenre>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<PremiumGenre>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<PremiumGenre> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class GenreDtoContentDtoPersistentListSerializer(
    private val serializer: KSerializer<GenreDto>,
) : KSerializer<PersistentList<GenreDto>> {

    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<GenreDto>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<GenreDto>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<GenreDto> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class PersistentListSerializer<T>(private val serializer: KSerializer<T>) : KSerializer<PersistentList<T>> {

    override val descriptor: SerialDescriptor = ListSerializer(serializer).descriptor

    override fun serialize(encoder: Encoder, value: PersistentList<T>) {
        ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<T> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}*/
