package by.pavvel.service.impl;

import by.pavvel.dao.EmployeeDao;
import by.pavvel.dao.impl.EmployeeDaoImpl;
import by.pavvel.exception.EmployeeNotFoundException;
import by.pavvel.model.Employee;
import by.pavvel.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeServiceImpl.class);

    private static EmployeeService instance;

    private final EmployeeDao employeeDao;

    public EmployeeServiceImpl(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public static synchronized EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeServiceImpl(EmployeeDaoImpl.getInstance());
        }
        return instance;
    }

    @Override
    public List<Employee> getEmployees() {
        logger.info("getEmployees was called: ");
        return employeeDao.findEmployees();
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        logger.info("getEmployeeById was called: {} ", employeeId);
        return employeeDao.findEmployeeById(employeeId).orElseThrow(() -> {
            EmployeeNotFoundException employeeNotFoundException = new EmployeeNotFoundException(
                    String.format("Employee with id %s doesn't exist", employeeId));
            logger.error("error in getEmployeeById: {} ", employeeId, employeeNotFoundException);
            return employeeNotFoundException;
        });
    }

    @Override
    public Employee addEmployee(Employee employee) {
        logger.info("addEmployee was called for employee: {} ", employee.getId());
        return employeeDao.saveEmployee(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        logger.info("updateEmployee was called for employee: {} ", employee.getId());
        getEmployeeById(employee.getId());
        return employeeDao.updateEmployee(employee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        logger.info("deleteEmployee was called for employee: {} ", employeeId);
        getEmployeeById(employeeId);
        employeeDao.deleteEmployee(employeeId);
    }
}
