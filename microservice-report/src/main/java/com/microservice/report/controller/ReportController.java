package com.microservice.report.controller;

import com.microservice.report.client.AuthFeignClient;
import com.microservice.report.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private AuthFeignClient authFeignClient;

    //Generate PDF report of users
    @Operation(
            summary = "Generate PDF report of users",
            description = "Generate and download a report in PDF format containing the complete list of users"
                    + "obtained from the authentication microservice. "
                    + "The user must be authenticated and have administrator permissions to access this resource.",
            tags = {"Reports"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Report generated correctly",
                            content = @Content(
                                    mediaType = "application/pdf",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — invalid or not provided token"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden — the user does not have permission to generate the report"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error while generating the report"
                    )
            }
    )
    @GetMapping("/users")
    public void generateReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authHeader = request.getHeader("Authorization");

        List<UserDto> users = authFeignClient.getAllUsers(authHeader);

        InputStream reportStream = getClass().getResourceAsStream("/reports/ReporteUsuarios.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}
