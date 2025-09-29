package com.movieproject.common;

import com.movieproject.domain.director.entity.Director;
import com.movieproject.domain.director.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DirectorDummyLoader implements CommandLineRunner {

    private final DirectorRepository directorRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Faker faker = new Faker(new Locale("ko")); // 한국어 이름
        List<Director> batch = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            Director director = Director.of(
                    faker.name().fullName(),
                    faker.country().name(),
                    faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
            batch.add(director);

            if (batch.size() == 1000) { // 1000개 단위로 저장
                directorRepository.saveAll(batch);
                directorRepository.flush();
                batch.clear();
                System.out.println(i + "명의 감독이 저장되었습니다.");
            }
        }
        System.out.println("✅ 10000명의 랜덤 감독 데이터 삽입 완료!");
    }
}
