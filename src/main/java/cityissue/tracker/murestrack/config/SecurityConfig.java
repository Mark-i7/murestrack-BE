package cityissue.tracker.murestrack.config;

import cityissue.tracker.murestrack.utils.JWTAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security.formLogin().disable();
        security.csrf().disable().authorizeRequests().anyRequest().permitAll();
//        security.csrf().disable()
//                .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/login").permitAll()
//                .antMatchers(HttpMethod.GET, "/websocket/**").permitAll()
//                .regexMatchers(HttpMethod.PATCH, "/users/status/\\w+(/)?").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .cors();
    }
}
