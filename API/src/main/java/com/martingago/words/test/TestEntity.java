package com.martingago.words.test;

import com.netflix.discovery.converters.Auto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "test_entity")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_entity_seq")
    @SequenceGenerator(name = "test_entity_seq", sequenceName = "test_entity_seq", allocationSize = 100)
    private Long id;

    private int num;
}

@Repository
interface TestEntityRepository extends JpaRepository<TestEntity, Long> {}

@RestController
@RequestMapping("api/v1/")
class TestEntityController {

    @Autowired
    private TestEntityRepository repository;

    @PostMapping("/bulk-insert")
    @Transactional
    public List<TestEntity> bulkInsert(@RequestParam int count) {
        List<TestEntity> entities = IntStream.rangeClosed(1, count)
                .mapToObj(i -> TestEntity.builder().num(i).build())
                .collect(Collectors.toList());
        return repository.saveAll(entities);
    }
}

