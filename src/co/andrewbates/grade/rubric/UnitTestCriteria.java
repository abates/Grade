package co.andrewbates.grade.rubric;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

import org.junit.runner.Result;

import co.andrewbates.grade.Student;
import co.andrewbates.grade.Submission;

public class UnitTestCriteria implements Criteria {
    class StudentClassLoader extends ClassLoader {
        URLClassLoader urlLoader;

        StudentClassLoader(Student student) {
            HashSet<URL> urls = new HashSet<URL>();
            for (Submission submission : student.getSubmissions()) {
                try {
                    urls.add(submission.getFile().toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            urlLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.startsWith("org.junit")) {
                return super.findClass(name);
            }
            return urlLoader.loadClass(name);
        }
    }

    @SuppressWarnings("rawtypes")
    Class[] testCases;

    @Override
    public void grade(Student student) {
        StudentClassLoader cl = new StudentClassLoader(student);
        try {
            Class<?> clazz = cl.loadClass("org.junit.runner.JUnitCore");
            Method runner = clazz.getMethod("runClasses", Class[].class);
            Result result = (Result) runner.invoke(null, testCases);
            System.out.println("Result: " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
