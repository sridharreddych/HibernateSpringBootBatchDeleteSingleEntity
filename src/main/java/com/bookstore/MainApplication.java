package com.bookstore;

import com.bookstore.service.BookstoreService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

    private final BookstoreService bookstoreService;

    public MainApplication(BookstoreService bookstoreService) {
        this.bookstoreService = bookstoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {

            bookstoreService.persistAuthors();
            // bookstoreService.deleteAuthorsViaDeleteAllInBatch();
            // bookstoreService.deleteAuthorsViaDeleteInBatch();                        
             bookstoreService.deleteAuthorsViaDeleteInBulk();
            // bookstoreService.deleteAuthorsViaDeleteAll();
           // bookstoreService.deleteAuthorsViaDelete();            
        };
    }
}

/*
 * How To Batch Deletes That Don't Involve Associations In MySQL

Description: Batch deletes that don't involve associations in MySQL.

Note: Spring deleteAllInBatch() and deleteInBatch() don't use delete batching and don't take advantage of optimstic locking mechanism to prevent lost updates. They rely on Query.executeUpdate() to trigger bulk operations. These operations are fast, but Hibernate doesn’t know which entities are removed, therefore, the Persistent Context is not updated accordingly (it is advisable to flush (before delete) and close/clear (after delete) the Persistent Context accordingly to avoid issues created by unflushed (if any) or outdated (if any) entities). The first one (deleteAllInBatch()) simply triggers a delete from entity_name statement and is very useful for deleting all records. The second one (deleteInBatch()) triggers a delete from entity_name where id=? or id=? or id=? ... statement, therefore, is prone to cause issues if the generated DELETE statement exceedes the maximum accepted size. This drawback can be controlled by deleting the data in chunks, relying on IN operator, and so on. Bulk operations are faster than batching which can be achieved via the deleteAll(), deleteAll(Iterable<? extends T> entities) or delete() method. Behind the scene, the two flavors of deleteAll() relies on delete(). The delete()/deleteAll() methods rely on EntityManager.remove() therefore the Persistent Context is synchronized accordingly. This time, batching is employed and optimstic locking mechanism to prevent lost updates can be used.

Key points for regular delete batching:

for deleting in batches rely on deleteAll(), deleteAll(Iterable<? extends T> entities) or delete() method
in application.properties set spring.jpa.properties.hibernate.jdbc.batch_size
in application.properties set JDBC URL with rewriteBatchedStatements=true (optimization for MySQL, statements get rewritten into a single string buffer and sent in a single request)
in application.properties set JDBC URL with cachePrepStmts=true (enable caching and is useful if you decide to set prepStmtCacheSize, prepStmtCacheSqlLimit, etc as well; without this setting the cache is disabled)
in application.properties set JDBC URL with useServerPrepStmts=true (this way you switch to server-side prepared statements (may lead to signnificant performance boost))
before Hibernate 5, we need to set in application.properties a setting for enabling batching for versioned entities during update and delete operations (entities that contains @Version for implicit optimistic locking); this setting is: spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true; starting with Hibernate 5, this setting should be true by default
Output example:

 
*/