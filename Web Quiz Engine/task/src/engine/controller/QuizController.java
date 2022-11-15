package engine.controller;

import engine.entity.Answer;
import engine.entity.CompletedQuiz;
import engine.entity.Feedback;
import engine.entity.Quiz;
import engine.repository.CompletedQuizRepository;
import engine.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.*;

@RestController
public class QuizController {

    @Autowired
    QuizRepository repository;

    @Autowired
    CompletedQuizRepository completedQuizRepository;



    @PostMapping("/api/quizzes")
    public Quiz createNewQuiz(@Valid @RequestBody Quiz newQuiz) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String author = userDetails.getUsername();
        newQuiz.setAuthor(author);
        return repository.save(newQuiz);
    }

    @GetMapping("/api/quizzes/{id}")
    public Quiz getQuizById(@PathVariable int id) {
        Optional<Quiz> quiz = repository.findById(id);
        if (quiz.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return quiz.get();
        }
    }

    @GetMapping("/api/quizzes")
    public Page<Quiz> getAllQuizzes(@RequestParam(defaultValue = "0") Integer page) {
        Pageable paging = PageRequest.of(page, 10);
        Page<Quiz> pageResult = repository.findAll(paging);
        if (pageResult.hasContent()) {
            return pageResult;
        } else {
            return Page.empty();
        }
    }

    @GetMapping("/api/quizzes/completed")
    public Page<CompletedQuiz> getAllCompletedQuiz(@RequestParam(defaultValue = "0") Integer page) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("completedAt").descending());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();
        Page<CompletedQuiz> pageResult = completedQuizRepository.findAllByName(user, paging);
        if (pageResult.hasContent()) {
            return pageResult;
        } else {
            return Page.empty();
        }
    }

    @DeleteMapping("/api/quizzes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable int id) {
        Optional<Quiz> optionalQuiz = repository.findById(id);
        if (optionalQuiz.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user = userDetails.getUsername();
            String author = optionalQuiz.get().getAuthor();
            if (!user.equals(author)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your quiz");
            }
            repository.delete(optionalQuiz.get());
        }

    }

    @PostMapping("/api/quizzes/{id}/solve")
    public Feedback solveQuiz(@RequestBody Answer answer, @PathVariable int id) {
        Optional<Quiz> quizOptional = repository.findById(id);

        if (quizOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Quiz quiz = quizOptional.get();

        List<Integer> correctAnswer = quiz.getAnswer();
        List<Integer> givenAnswer = answer.getAnswer();

        if (givenAnswer == null) {
            givenAnswer = new ArrayList<>();
        }
        if (correctAnswer == null) {
            correctAnswer = new ArrayList<>();
        }

        if (givenAnswer.size() != correctAnswer.size()) {
            return new Feedback(false, "Wrong answer! Please, try again.");
        }



        if (new HashSet<>(givenAnswer).equals(new HashSet<>(correctAnswer))) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user = userDetails.getUsername();
            completedQuizRepository.save(new CompletedQuiz(quiz.getId(), user));
            return new Feedback(true, "Congratulations, you're right!");
        } else {
            return new Feedback(false, "Wrong answer! Please, try again.");
        }
    }
}
