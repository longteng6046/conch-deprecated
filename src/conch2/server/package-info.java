/**
 * Conch is a repository designed to share basic information about
 * component-based systems as well as test records of such systems
 * from different parts within a loosely-coupled software
 * community. It is built aiming to achieve the following goals:
 * <p>
 * <ol>
 * <li> Basic Metadata about components should be consistent and shared
 * among members of a community, thus the components used by them and
 * their testing results are comparable.</li>
 * <li> Building and runtime options of components should be specified
 * and shared among the community as well, so tests with the same set
 * of configuration conducted by different parts are comparable.</li>
 * <li> Dependency relationships of components should be shared within
 * the whole community, thus different members can help building and
 * easily access a complete set of Component Dependency Graphs(CDGs).</li>
 * <li>When conducting a test using metadata provided from Conch, the
 * results and configuration used can be stored in Conch and shared by
 * all members.</li>
 * </ol>
 * <p>
 * This document introduces what APIs are provided by Conch. The APIs
 * are simply categorized into two groups:
 * <ol>
 * <li> User APIs: ordinary community members can provide data to Conch
 * using these APIs. The data include metadata of components,
 * packages, building flags of components/packages, runtime flags of
 * components/packages, available test suites/cases, users' testing
 * configurations and results.</li>
 * <li> Administrator APIs: system administrators can use them to
 * manange authentication data, correct information, merge data, and
 * delete records.</li>
 * </ol>
 * <p>
 * In the following sections, we will introduce both categories in
 * details.
 * <p>
 * By the way, this is the second version of Conch.
 * <p>
 */
package conch2.server;

