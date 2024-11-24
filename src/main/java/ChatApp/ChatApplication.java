package ChatApp;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootApplication
//@EnableJpaRepositories
@EnableScheduling
public class ChatApplication {

	@PersistenceContext
	private EntityManager entityManager;


	public static void main(String[] args) {

		SpringApplication.run(ChatApplication.class, args);
	}

}
