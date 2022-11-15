package engine.repository;

import engine.entity.CompletedQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompletedQuizRepository extends PagingAndSortingRepository<CompletedQuiz, Integer> {
    Page<CompletedQuiz> findAllByName(String name, Pageable pageable);

}
