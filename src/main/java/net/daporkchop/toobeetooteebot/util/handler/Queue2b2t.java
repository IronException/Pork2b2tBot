package net.daporkchop.toobeetooteebot.util.handler;

import java.util.Optional;

import static net.daporkchop.toobeetooteebot.util.Constants.CACHE;
import static net.daporkchop.toobeetooteebot.util.Constants.CONFIG;

/**
 * this class is specifically and should only be used when connecting to 2b2t.org
 */
public class Queue2b2t {


    public Optional<String> calculateQueueData(final String desiredText) {
        if (!"2b2t.org".equals(CONFIG.client.server.address)) {
            return Optional.empty();
        }
        if(CACHE.getChunkCache().size() > 0) {
            return Optional.empty();
        }
        final String text = CACHE.getTabListCache().getTabList().getHeader();
        final String pos = text.replaceAll(".*Position in queue: §l", "")
                .replaceAll("§6.*", ""); // TODO the \n is still in the string as an "n" and idk how to remove it
        final String time = text.replaceAll(".*Estimated time: §l", "")
                .replaceAll("\n.*", ""); // TODO this seems to do nothing since "n\"}" is still in the text :/

        return Optional.of(desiredText.replace("{pos}", pos)
                .replace("{time}", time));
    }

}
