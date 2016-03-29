SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `conch` ;
CREATE SCHEMA IF NOT EXISTS `conch` DEFAULT CHARACTER SET utf8 ;
USE `conch` ;

-- -----------------------------------------------------
-- Table `conch`.`ComponentVendor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`ComponentVendor` ;

CREATE  TABLE IF NOT EXISTS `conch`.`ComponentVendor` (
  `name` VARCHAR(45) NOT NULL ,
  `note` VARCHAR(450) NULL ,
  `website` VARCHAR(450) NULL ,
  PRIMARY KEY (`name`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`ComponentCategory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`ComponentCategory` ;

CREATE  TABLE IF NOT EXISTS `conch`.`ComponentCategory` (
  `name` VARCHAR(45) NOT NULL ,
  `note` VARCHAR(450) NULL ,
  PRIMARY KEY (`name`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`ComponentMeta`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`ComponentMeta` ;

CREATE  TABLE IF NOT EXISTS `conch`.`ComponentMeta` (
  `ComponentId` INT NOT NULL AUTO_INCREMENT ,
  `ComponentName` VARCHAR(45) NOT NULL ,
  `ComponentVendor` VARCHAR(45) NULL ,
  `ComponentCategory` VARCHAR(45) NULL ,
  `ComponentConstraints` VARCHAR(1000) NULL ,
  PRIMARY KEY (`ComponentName`) ,
  UNIQUE INDEX `ComponentName_UNIQUE` (`ComponentName` ASC) ,
  UNIQUE INDEX `ComponentId_UNIQUE` (`ComponentId` ASC) ,
  INDEX `fk_ComponentMeta_ComponentVendor1_idx` (`ComponentVendor` ASC) ,
  INDEX `fk_ComponentMeta_ComponentCategory1_idx` (`ComponentCategory` ASC) ,
  CONSTRAINT `fk_ComponentMeta_ComponentVendor1`
    FOREIGN KEY (`ComponentVendor` )
    REFERENCES `conch`.`ComponentVendor` (`name` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ComponentMeta_ComponentCategory1`
    FOREIGN KEY (`ComponentCategory` )
    REFERENCES `conch`.`ComponentCategory` (`name` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`PackageInfo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`PackageInfo` ;

CREATE  TABLE IF NOT EXISTS `conch`.`PackageInfo` (
  `PackageId` INT NOT NULL AUTO_INCREMENT ,
  `ComponentName` VARCHAR(45) NOT NULL ,
  `ComponentVersion` VARCHAR(45) NOT NULL ,
  `ComponentArch` VARCHAR(45) NOT NULL ,
  `ComponentUrl` LONGTEXT NOT NULL ,
  `UntarDirName` VARCHAR(45) NOT NULL ,
  `ReleaseDate` DATE NOT NULL ,
  PRIMARY KEY (`PackageId`, `ComponentName`) ,
  UNIQUE INDEX `ReleaseDate_UNIQUE` (`ReleaseDate` ASC) ,
  INDEX `fk_PackageInfo_ComponentMeta1` (`ComponentName` ASC) ,
  CONSTRAINT `fk_PackageInfo_ComponentMeta1`
    FOREIGN KEY (`ComponentName` )
    REFERENCES `conch`.`ComponentMeta` (`ComponentName` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`Nodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`Nodes` ;

CREATE  TABLE IF NOT EXISTS `conch`.`Nodes` (
  `NodeId` INT NOT NULL AUTO_INCREMENT ,
  `NodeType` ENUM('OP', 'COMP') NOT NULL ,
  `ComponentId` INT NULL ,
  `OperatorType` ENUM('AND','XOR') NULL ,
  PRIMARY KEY (`NodeId`) ,
  INDEX `fk_Nodes_ComponentMeta1_idx` (`ComponentId` ASC) ,
  CONSTRAINT `fk_Nodes_ComponentMeta1`
    FOREIGN KEY (`ComponentId` )
    REFERENCES `conch`.`ComponentMeta` (`ComponentId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Nodes within a CDG.';


-- -----------------------------------------------------
-- Table `conch`.`Edges`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`Edges` ;

CREATE  TABLE IF NOT EXISTS `conch`.`Edges` (
  `PNodeId` INT NOT NULL ,
  `CNodeId` INT NOT NULL ,
  `constraints` VARCHAR(500) NULL ,
  PRIMARY KEY (`PNodeId`, `CNodeId`) ,
  INDEX `fk_Edges_Nodes2_idx` (`CNodeId` ASC) ,
  CONSTRAINT `fk_Edges_Nodes1`
    FOREIGN KEY (`PNodeId` )
    REFERENCES `conch`.`Nodes` (`NodeId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Edges_Nodes2`
    FOREIGN KEY (`CNodeId` )
    REFERENCES `conch`.`Nodes` (`NodeId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Edges within a CDG';


-- -----------------------------------------------------
-- Table `conch`.`TestSuites`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`TestSuites` ;

CREATE  TABLE IF NOT EXISTS `conch`.`TestSuites` (
  `SuiteId` INT NOT NULL AUTO_INCREMENT ,
  `SuiteName` VARCHAR(45) NOT NULL ,
  `ComponentName` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`SuiteName`, `ComponentName`) ,
  INDEX `fk_TestSuites_ComponentMeta1` (`ComponentName` ASC) ,
  UNIQUE INDEX `SuiteId_UNIQUE` (`SuiteId` ASC) ,
  CONSTRAINT `fk_TestSuites_ComponentMeta1`
    FOREIGN KEY (`ComponentName` )
    REFERENCES `conch`.`ComponentMeta` (`ComponentName` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Test cases are organized by test suties, and each test suite' /* comment truncated */;


-- -----------------------------------------------------
-- Table `conch`.`TestCases`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`TestCases` ;

CREATE  TABLE IF NOT EXISTS `conch`.`TestCases` (
  `TestId` INT NOT NULL AUTO_INCREMENT ,
  `SuiteName` VARCHAR(45) NOT NULL ,
  `LocalName` VARCHAR(45) NULL ,
  `TestNote` VARCHAR(500) NULL ,
  PRIMARY KEY (`TestId`, `SuiteName`) ,
  INDEX `fk_TestCases_TestSuites1_idx` (`SuiteName` ASC) ,
  UNIQUE INDEX `LocalName_UNIQUE` (`LocalName` ASC) ,
  CONSTRAINT `fk_TestCases_TestSuites1`
    FOREIGN KEY (`SuiteName` )
    REFERENCES `conch`.`TestSuites` (`SuiteName` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`Testers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`Testers` ;

CREATE  TABLE IF NOT EXISTS `conch`.`Testers` (
  `TesterId` INT NOT NULL AUTO_INCREMENT ,
  `Name` VARCHAR(45) NOT NULL ,
  `Permission` VARCHAR(45) NULL ,
  `Notes` VARCHAR(500) NULL ,
  PRIMARY KEY (`TesterId`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`TestRecord`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`TestRecord` ;

CREATE  TABLE IF NOT EXISTS `conch`.`TestRecord` (
  `RecordId` INT NOT NULL AUTO_INCREMENT ,
  `PackageId` INT NOT NULL ,
  `TesterId` INT NOT NULL ,
  `TestType` ENUM('build', 'functional') NOT NULL ,
  `Configuration` VARCHAR(1000) NOT NULL ,
  `SubmitTime` DATETIME NULL ,
  PRIMARY KEY (`RecordId`) ,
  INDEX `fk_TestRecord_PackageInfo1_idx` (`PackageId` ASC) ,
  INDEX `fk_TestRecord_Testers1_idx` (`TesterId` ASC) ,
  CONSTRAINT `fk_TestRecord_PackageInfo1`
    FOREIGN KEY (`PackageId` )
    REFERENCES `conch`.`PackageInfo` (`PackageId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_TestRecord_Testers1`
    FOREIGN KEY (`TesterId` )
    REFERENCES `conch`.`Testers` (`TesterId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`FuncTestResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`FuncTestResults` ;

CREATE  TABLE IF NOT EXISTS `conch`.`FuncTestResults` (
  `RecordId` INT NOT NULL ,
  `TestId` INT NOT NULL ,
  `Result` ENUM('PASSED', 'FAILED', 'UNTESTABLE' ) NOT NULL ,
  `FuncTestResultscol` VARCHAR(45) NULL ,
  PRIMARY KEY (`RecordId`, `TestId`) ,
  INDEX `fk_TestResults_TestRecord1_idx` (`RecordId` ASC) ,
  CONSTRAINT `fk_TestResults_TestCases1`
    FOREIGN KEY (`TestId` )
    REFERENCES `conch`.`TestCases` (`TestId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_TestResults_TestRecord1`
    FOREIGN KEY (`RecordId` )
    REFERENCES `conch`.`TestRecord` (`RecordId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`BuildtestResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`BuildtestResults` ;

CREATE  TABLE IF NOT EXISTS `conch`.`BuildtestResults` (
  `RecordId` INT NOT NULL ,
  `ParentPkgId` INT NOT NULL ,
  `ChildPkgId` INT NOT NULL ,
  `Result` ENUM('SUCCESS', 'FAILURE','N/A') NOT NULL ,
  PRIMARY KEY (`RecordId`, `ParentPkgId`, `ChildPkgId`) ,
  INDEX `fk_BuildtestResults_PackageInfo1_idx` (`ParentPkgId` ASC) ,
  INDEX `fk_BuildtestResults_PackageInfo2_idx` (`ChildPkgId` ASC) ,
  CONSTRAINT `fk_BuildtestResults_PackageInfo1`
    FOREIGN KEY (`ParentPkgId` )
    REFERENCES `conch`.`PackageInfo` (`PackageId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_BuildtestResults_PackageInfo2`
    FOREIGN KEY (`ChildPkgId` )
    REFERENCES `conch`.`PackageInfo` (`PackageId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`Session`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`Session` ;

CREATE  TABLE IF NOT EXISTS `conch`.`Session` (
  `SessionId` INT NOT NULL AUTO_INCREMENT ,
  `TesterId` INT NOT NULL ,
  `ActiveStatus` TINYINT(1) NOT NULL ,
  `AssignedTime` DATETIME NOT NULL ,
  `LogoutTime` DATETIME NULL ,
  INDEX `fk_Session_Testers1_idx` (`TesterId` ASC) ,
  PRIMARY KEY (`SessionId`) ,
  CONSTRAINT `fk_Session_Testers1`
    FOREIGN KEY (`TesterId` )
    REFERENCES `conch`.`Testers` (`TesterId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `conch`.`CoverageData`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `conch`.`CoverageData` ;

CREATE  TABLE IF NOT EXISTS `conch`.`CoverageData` (
  `coveredPackage` INT NOT NULL ,
  `RecordId` INT NOT NULL ,
  `TestId` INT NOT NULL ,
  `covData` MEDIUMBLOB NOT NULL ,
  INDEX `fk_table1_PackageInfo1` (`coveredPackage` ASC) ,
  PRIMARY KEY (`coveredPackage`, `RecordId`, `TestId`) ,
  INDEX `fk_table1_FuncTestResults1` (`RecordId` ASC, `TestId` ASC) ,
  CONSTRAINT `fk_table1_PackageInfo1`
    FOREIGN KEY (`coveredPackage` )
    REFERENCES `conch`.`PackageInfo` (`PackageId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_table1_FuncTestResults1`
    FOREIGN KEY (`RecordId` , `TestId` )
    REFERENCES `conch`.`FuncTestResults` (`RecordId` , `TestId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
