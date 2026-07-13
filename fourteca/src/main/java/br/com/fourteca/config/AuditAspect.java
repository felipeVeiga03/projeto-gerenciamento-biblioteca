package br.com.fourteca.config;

import br.com.fourteca.entity.Usuario;
import br.com.fourteca.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditoriaService auditoriaService;

    @AfterReturning(pointcut = "@annotation(br.com.fourteca.config.Auditable)", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String acao = auditable.acao();
        String nomeEntidade = "Unknown";
        Long entidadeId = null;

        if (result != null) {
            try {
                Method getId = result.getClass().getMethod("getId");
                entidadeId = (Long) getId.invoke(result);
                nomeEntidade = result.getClass().getSimpleName().replace("Response", "");
            } catch (Exception e) {
                // Ignora se não houver método getId
            }
        } else {
             // Tenta obter o ID do primeiro argumento do método (para exclusões)
            if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof Number) {
                entidadeId = ((Number) joinPoint.getArgs()[0]).longValue();
                nomeEntidade = signature.getDeclaringType().getSimpleName().replace("Service", "");
            }
        }

        auditoriaService.registrarAuditoria(usuario.getId(), acao, nomeEntidade, entidadeId);
    }
}
