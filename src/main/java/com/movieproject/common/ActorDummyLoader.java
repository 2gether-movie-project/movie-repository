package com.movieproject.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.movieproject.domain.actor.entity.Actor;
import com.movieproject.domain.actor.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Profile("dummy")
public class ActorDummyLoader implements CommandLineRunner {

    private final ActorRepository actorRepository;

    @Override
    @Transactional
    public void run(String... args) {
        ((Logger) LoggerFactory.getLogger("org.hibernate.SQL")).setLevel(Level.OFF);
        ((Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder")).setLevel(Level.OFF);
        ((Logger) LoggerFactory.getLogger("org.hibernate.engine.jdbc.batch.internal.BatchingBatch")).setLevel(Level.OFF);

        Faker faker = new Faker(new Locale("ko"));
        List<Actor> batch = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            Actor actor = Actor.of(
                    faker.name().fullName(),
                    faker.country().name(),
                    faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
            batch.add(actor);

            if (batch.size() == 1000) { // 1000개 단위로 저장
                actorRepository.saveAll(batch);
                actorRepository.flush();
                batch.clear();
                System.out.println(i + "명의 배우가 저장되었습니다.");
            }
        }
        System.out.println("50000명의 랜덤 배우 데이터가 삽입되었습니다!");

    }
}
