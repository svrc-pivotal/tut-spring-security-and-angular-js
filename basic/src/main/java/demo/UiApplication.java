package demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.apache.tomcat.util.IntrospectionUtils.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UiApplication {



	@RequestMapping("/resource")
	public Map<String,Object> home() {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", "Hello World");
		return model;
	}



  @Autowired
  Environment env;

  private Map<String, Boolean> features;

  @Bean
  public Map<String, Boolean>  features() {
    if (features != null)
      return features;
    Map<String, Boolean>  props = new HashMap<String, Boolean> ();
    MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
    StreamSupport.stream(propSrcs.spliterator(), false)
        .filter(ps -> ps instanceof EnumerablePropertySource)
        .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
        .flatMap(Arrays::<String>stream)
        .forEach(propName -> {
          if (propName.toLowerCase().startsWith("feature_"))
            props.put(propName.toLowerCase().substring("feature_".length()), new Boolean(env.getProperty(propName)));
        });
    features = props;
    return props;
  }
   
	@RequestMapping("/features/{id}")
	public boolean feature(@PathVariable("id") String id) {
    // Only allow alphanumeric, dashes, underscores, length 100, case insensitive
    if (id == null || ! id.matches("^[a-zA-Z0-9-_]+$") || id.length() > 100 )
      return false;
		Boolean feature = features().get(id.toLowerCase());
    if (feature == null)
      return false;
    return feature;
  }

	@RequestMapping("/features")
	public Map<String,Boolean> allFeatures() {   
    return features();
  }


	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}

}
