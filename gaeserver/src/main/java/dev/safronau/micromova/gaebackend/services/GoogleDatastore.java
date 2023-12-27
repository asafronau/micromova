package dev.safronau.micromova.gaebackend.services;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.api.services.datastore.v1.model.Entity;
import com.google.api.services.datastore.v1.model.Value;
import com.google.common.flogger.FluentLogger;
import dev.safronau.micromova.proto.Language;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GoogleDatastore {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  record IdAndTtl(String id, Instant ttl) {}

  private final ConcurrentHashMap<String, IdAndTtl> validAccountCache = new ConcurrentHashMap<>();

  private static final Duration CACHE_TTL = Duration.ofHours(1);

  private final DataStore dataStore;
  private final Clock clock;

  @Inject
  public GoogleDatastore(DataStore dataStore, Clock clock) {
    this.dataStore = dataStore;
    this.clock = clock;
  }

  public boolean isUserValid(String userKey, String userId) {
    if (userKey.isBlank() || userId.isBlank()) {
      return false;
    }
    Instant now = clock.instant();
    if (validAccountCache.containsKey(userKey)) {
      IdAndTtl value = validAccountCache.get(userKey);
      if (value.id().equals(userId) && value.ttl().plus(CACHE_TTL).isAfter(now)) {
        return true;
      }
    }
    String storedId = getUserId(userKey);
    boolean isValidId = storedId.equals(userId);
    if (isValidId) {
      validAccountCache.put(userKey, new IdAndTtl(userId, now.plus(CACHE_TTL)));
    }
    return isValidId;
  }

  public long putPhrase(String text, Language language, String femaleMp3, String maleMp3) {
    logger.atInfo().log("Storing phrase(%s, %s, %s, %s)", text, language, femaleMp3, maleMp3);
    long keyId = getPhraseKey(text, language);
    dataStore.put(
        Constants.PHRASE_KEY_KIND,
        keyId,
        new Entity()
            .setProperties(
                Map.of(
                    "text", new Value().setStringValue(text),
                    "language", new Value().setIntegerValue((long) language.getNumber()),
                    "female_mp3_path", new Value().setStringValue(femaleMp3),
                    "male_mp3_path", new Value().setStringValue(maleMp3))));
    return keyId;
  }

  public String getUserId(String userKey) {
    return getUser(userKey)
        .map(entity -> entity.getProperties().get("userName").getStringValue())
        .orElse("");
  }

  private Optional<Entity> getUser(String userKey) {
    return dataStore.get(Constants.USER_KEY_KIND, userKey);
  }

  public Optional<Entity> getPhrase(String text, Language language) {
    logger.atInfo().log("Querying phrase(%s, %s)", text, language);
    return dataStore.get(Constants.PHRASE_KEY_KIND, getPhraseKey(text, language));
  }

  private static long getPhraseKey(String text, Language language) {
    return fnv1((text + language.toString()).getBytes(UTF_8));
  }

  private static long fnv1(byte[] data) {
    long hash = 0xcbf29ce484222325L;
    for (byte datum : data) {
      hash *= 0x100000001b3L;
      hash ^= datum & 0xff;
    }
    return hash;
  }
}
