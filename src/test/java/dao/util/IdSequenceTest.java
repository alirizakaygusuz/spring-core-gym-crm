package dao.util;

import com.alirizakaygusuz.gymcrm.dao.util.IdSequence;
import com.alirizakaygusuz.gymcrm.service.validator.CommonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IdSequenceTest {

    @Test
    void next_shouldReturnIncrementedIds() {
        IdSequence idSequence = new IdSequence();
        long firstId = idSequence.next();
        long secondId = idSequence.next();


        assert(firstId == 1L);
        assert(secondId == 2L);
    }

    @Test
    void syncFrom_shouldSetNextIdCorrectly() {
        IdSequence idSequence = new IdSequence();
        Map<Long, String> storage = Map.of(
                1L, "Item1",
                2L, "Item2",
                5L, "Item5"
        );

        idSequence.syncFrom(storage);
        long nextId = idSequence.next();

        assert(nextId == 6L);
    }
}
