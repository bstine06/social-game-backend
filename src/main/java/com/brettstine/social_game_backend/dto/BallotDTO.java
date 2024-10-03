package com.brettstine.social_game_backend.dto;

import java.util.List;

public class BallotDTO {
    
    private QuestionDTO question;
    private List<AnswerDTO> answers;

    public BallotDTO(QuestionDTO question, List<AnswerDTO> answers) {
        this.question = question;
        this.answers = answers;
    }

    public QuestionDTO getQuestion() {
        return this.question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public List<AnswerDTO> getAnswers() {
        return this.answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

}
