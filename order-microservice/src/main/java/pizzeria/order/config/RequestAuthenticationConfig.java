package pizzeria.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pizzeria.order.authentication.JwtAuthenticationEntryPoint;
import pizzeria.order.authentication.JwtRequestFilter;

/**
 * The type Web security config.
 */
@Configuration
public class RequestAuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final transient JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final transient JwtRequestFilter jwtRequestFilter;

    public RequestAuthenticationConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                       JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String authorizedRole = "ROLE_MANAGER";
        //here we validate that the user is authenticated and exists in the system
        //we also make the manager only endpoints visible to only the managers
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/order/place").authenticated()
                .antMatchers("/order/list").authenticated()
                .antMatchers("/order/delete").authenticated()
                .antMatchers("/order/edit").authenticated()
                .antMatchers("/order/listAll").hasAuthority(authorizedRole)
                .antMatchers("/coupon/create").hasAuthority(authorizedRole)
                .antMatchers("/store/create").hasAuthority(authorizedRole)
                .antMatchers("/store/edit").hasAuthority(authorizedRole)
                .antMatchers("/store/delete").hasAuthority(authorizedRole)
                .antMatchers("/store/get_stores").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}