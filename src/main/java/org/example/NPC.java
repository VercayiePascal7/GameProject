package org.example;

public class NPC {
    private String name;
    private TalkStrategy talkStrategy;

    public NPC(String name, TalkStrategy talkStrategy) {
        this.name = name;
        this.talkStrategy = talkStrategy;
    }

    public void talk(Player player) {
        System.out.println(name + ": " + talkStrategy.getDialogue(player));
    }

    public String getName() {
        return name;
    }
}