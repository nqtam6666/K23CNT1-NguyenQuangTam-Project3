package k23cnt1.nqt.lesson05.nqt_lesson05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class NqtLesson05Application {

	public static void main(String[] args) {
		SpringApplication.run(NqtLesson05Application.class, args);
	}

}
