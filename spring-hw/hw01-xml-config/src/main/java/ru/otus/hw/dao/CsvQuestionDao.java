package ru.otus.hw.dao;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final String ERROR = "При обработке файла возникла непредвиденная ошибка";
    private final TestFileNameProvider fileNameProvider;


    @Override
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        try {
            InputStream resourceAsStream = CsvQuestionDao.class.getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName());
            assert resourceAsStream != null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
                CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
                CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).withSkipLines(1).build();
                CsvToBean<QuestionDto> cb = new CsvToBeanBuilder<QuestionDto>(csvReader)
                        .withType(QuestionDto.class)
                        .build();
                cb.stream().forEach(questionDto -> questions.add(questionDto.toDomainObject()));
            }
        } catch (IOException e) {
            throw new QuestionReadException(ERROR, e);
        }
        return questions;
    }
}
