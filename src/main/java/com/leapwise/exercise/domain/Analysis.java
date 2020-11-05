package com.leapwise.exercise.domain;

import javax.persistence.*;

@Entity
@Table(name = "analysis")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String uuid;
    private String word;
    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
