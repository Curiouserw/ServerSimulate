package simu_anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ActionAnnotation{
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Action{
		public String name();
	}
}
