package com.udacity.jwdnd.course1.cloudstorage.Exceptions;

import com.udacity.jwdnd.course1.cloudstorage.Model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.Model.Notes;
import com.udacity.jwdnd.course1.cloudstorage.Model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.FilesService;
import com.udacity.jwdnd.course1.cloudstorage.services.NotesService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
public class ExceptionController  implements HandlerExceptionResolver{
    @Autowired
    private NotesService notesService;
    @Autowired
    private UserService userService;
    @Autowired
    private CredentialsService credentialsService;

    private User user;
    @Autowired
    private FilesService filesService;



    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundExceptionHandle(Model model)
    {
        model.addAttribute("error","URL Not Found");
        setModelAttributes(model);
        return "home";
    }

    public void setModelAttributes(Model model)
    {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                user=userService.getUser(authentication.getName());
            }
            model.addAttribute("newNote",new Notes());
            model.addAttribute("newCredential",new Credentials());
            model.addAttribute("Notes",notesService.getAllNotes(user.getUserid()));
            model.addAttribute("credentials",credentialsService.getCredentials(user.getUserid()));
            model.addAttribute("newFiles",filesService.getFilesData(user.getUserid()));
        }
        catch (Exception e)
        {
            System.out.println(" msg "+e);
        }

    }

    public void setModelAndView(ModelAndView modelAndView){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                user=userService.getUser(authentication.getName());
            }
            modelAndView.addObject("newNote",new Notes());
            modelAndView.addObject("newCredential",new Credentials());
            modelAndView.addObject("Notes",notesService.getAllNotes(user.getUserid()));
            modelAndView.addObject("credentials",credentialsService.getCredentials(user.getUserid()));
            modelAndView.addObject("newFiles",filesService.getFilesData(user.getUserid()));
        }
        catch (Exception e)
        {
            System.out.println(" msg "+e);
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        ModelAndView modelAndView = new ModelAndView();

        if(e instanceof MaxUploadSizeExceededException ){
            modelAndView = new ModelAndView();
            setModelAndView(modelAndView);
            modelAndView.addObject("error", "File too large too large too upload");
            modelAndView.setViewName("home.html");
            return modelAndView;
        }
        modelAndView.setViewName("home.html");
        return modelAndView;
    }
}
