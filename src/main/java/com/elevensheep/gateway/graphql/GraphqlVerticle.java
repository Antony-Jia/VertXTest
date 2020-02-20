package com.elevensheep.gateway.graphql;

import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.VertxDataFetcher;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static java.util.stream.Collectors.toList;

public class GraphqlVerticle extends AbstractVerticle{

    private final Logger logger = LogManager.getLogger(GraphqlVerticle.class);

    private List<String> links;
    @Override
    public void start() {
    prepareData();

    Router router = Router.router(vertx);
    router.route("/graphql").handler(GraphQLHandler.create(createGraphQL()));

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080);
    }

    private void prepareData() {

    links = new ArrayList<>();

    }

    private GraphQL createGraphQL() {
    String schema = vertx.fileSystem().readFileBlocking("links.graphqls").toString();

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = newRuntimeWiring()
        .type("Query", builder -> {
        VertxDataFetcher<List<String>> getAllLinks = new VertxDataFetcher<>(this::getAllLinks);
        return builder.dataFetcher("allLinks", getAllLinks);
        })
        .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema)
        .build();
    }

    private void getAllLinks(DataFetchingEnvironment env, Promise<List<String>> future) {
    boolean secureOnly = env.getArgument("secureOnly");
    List<String> result = links.stream()
        .filter(link -> !secureOnly)
        .collect(toList());
        future.complete(result);
    }
}