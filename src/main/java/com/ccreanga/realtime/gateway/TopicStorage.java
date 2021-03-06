package com.ccreanga.realtime.gateway;

import java.util.List;

public interface TopicStorage {

    List<String> getTopics(Long match);

    List<String> getAllTopics();

}
