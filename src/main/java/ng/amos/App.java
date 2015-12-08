package ng.amos;

import com.graphaware.module.uuid.UuidConfiguration;
import com.graphaware.module.uuid.UuidModule;
import com.graphaware.runtime.GraphAwareRuntime;
import com.graphaware.runtime.GraphAwareRuntimeFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;

import java.io.File;


public class App {
    public static void main(String[] args) {
        GraphDatabaseService neo4jdb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File("test.db"))
                .newGraphDatabase();
        GraphAwareRuntime runtime = GraphAwareRuntimeFactory.createRuntime(neo4jdb);  //where database is an 
        // instance of GraphDatabaseService
        UuidModule module = new UuidModule("UUIDM",
                UuidConfiguration.defaultConfiguration().withUuidProperty("uuid").withUuidIndex("uuidIndex"),
                neo4jdb);
        runtime.registerModule(module);
        runtime.start();
        runtime.waitUntilStarted();

        try (Transaction tx = neo4jdb.beginTx()) {
            neo4jdb.createNode();
            tx.success();
        }

        try (Transaction tx = neo4jdb.beginTx()) {
            String[] indices = neo4jdb.index().nodeIndexNames();

            if (indices.length == 0) {
                System.err.println("There are no indices! Shouldn't there be a uuidIndex?");
            }

            for (String index : indices) {
                    System.err.println(index);
            }
        }

        neo4jdb.shutdown();
        System.exit(0);
    }
}
