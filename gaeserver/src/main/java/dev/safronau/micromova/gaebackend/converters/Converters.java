package dev.safronau.micromova.gaebackend.converters;

import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.CollectionView;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.PhraseHeadline;
import java.util.Comparator;

public final class Converters {
  private Converters() {}

  public static CollectionView from(Collection collection) {
    CollectionView.Builder builder =
        CollectionView.newBuilder()
            .setName(collection.getName())
            .setUserId(collection.getUserId())
            .setSourceLanguage(collection.getSourceLanguage())
            .setTranslationLanguage(collection.getTranslationLanguage())
            .setDiscoverPoints(collection.getDiscoverPoints())
            .setCurrentDiscoverPoints(collection.getCurrentDiscoverPoints())
            .setIsDiscoverEnabled(collection.getIsDiscoverEnabled())
            .setVersion(collection.getVersion());
    collection.getPhraseList().stream()
        .map(Converters::from)
        .sorted(Comparator.comparingInt(PhraseHeadline::getStarScoreMillis))
        .forEach(builder::addPhrase);
    return builder.build();
  }

  public static PhraseHeadline from(Phrase phrase) {
    return PhraseHeadline.newBuilder()
        .setId(phrase.getId())
        .setNormalizedText(phrase.getNormalizedText())
        .setStarScoreMillis(calculateStars(phrase.getScore()))
        .setIsDiscoverable(phrase.getIsDiscoverable())
        .build();
  }

  private static int calculateStars(int score) {
    double starScore = Math.pow(score / 1000.0, 0.5) / 2;
    return (int) Math.min(3000, Math.round(starScore * 1000));
  }
}
