package org.example;

public class DefaultTalkStrategy implements TalkStrategy {
    private final String dialogue;

    public DefaultTalkStrategy(String dialogue) {
        this.dialogue = dialogue;
    }

    @Override
    public String getDialogue(Player player) {
        return dialogue;
    }
}