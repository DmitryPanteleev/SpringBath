package ru.homework.dpanteleev.springbath.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.homework.dpanteleev.springbath.model.Beer;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    @ChangeSet(order = "000", id = "dropDB", author = "dpanteleev", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initBeer", author = "dpanteleevt", runAlways = true)
    public void initBeer(MongockTemplate template) {
        for (int i = 0; i < 2; i++) {
            System.out.println("Save"+i);
            template.save(new Beer((long) i, "ImperialStout", 0.5));
        }
    }
}
