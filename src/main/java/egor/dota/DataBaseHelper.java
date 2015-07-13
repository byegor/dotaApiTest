package egor.dota;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Egor on 10.07.2015.
 */
public enum DataBaseHelper {
    INSTANCE;


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    static final String DB_URL = "jdbc:mysql://localhost/dota";
    static final String USER = "root";
    static final String PASS = "q1w2e3r4";


    static final String HERO_INSERT = "INSERT HERO (id, name) values(%s, '%s');";
    static final String HERO_GET_ALL = "SELECT * FROM HERO;";

    static final String TEAM_GET_BY_CODE = "SELECT * FROM TEAM WHERE CODE='%s';";
    static final String TEAM_INSERT = "INSERT TEAM (code) values('%s');";

    static final String TEAM_FIGHTS_INSERT = "INSERT team_fights (winner, looser, match_id) values(%s, %s, %s);";

    static final String PROGRESS_INSERT = "INSERT PROGRESS (value) values(%s);";
    static final String PROGRESS_GET_LAST = "SELECT * FROM PROGRESS ORDER BY id DESC LIMIT 1";

    DataBaseHelper() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void insertHeroes(Collection<Hero> heroes) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            for (Hero hero : heroes) {
                statement.addBatch(String.format(HERO_INSERT, hero.getId(), hero.getName()));
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public void insertMatchResults(Collection<MatchResult> results, long lastMatchId) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            for (MatchResult matchResult : results) {
                int winner = matchResult.radiantWin ? matchResult.radiantId : matchResult.direId;
                int looser = matchResult.radiantWin ? matchResult.direId : matchResult.radiantId;
                statement.addBatch(String.format(TEAM_FIGHTS_INSERT, winner, looser, matchResult.matchId));
            }
            statement.addBatch(String.format(PROGRESS_INSERT, lastMatchId));
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public List<Hero> getHeroes() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            List<Hero> heroes = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(HERO_GET_ALL);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                heroes.add(new Hero(id, name));
            }
            return heroes;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public int getTeamIdByCode(String code) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(TEAM_GET_BY_CODE, code));
            if (!resultSet.next()) {
                return -1;
            } else {
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public int createTeam(String code) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            //todo get generated id
            statement.executeUpdate(String.format(TEAM_INSERT, code));
            ResultSet resultSet = statement.executeQuery(String.format(TEAM_GET_BY_CODE, code));
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }
    public long getLatRecordedMatchId() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(PROGRESS_GET_LAST);
            if (!resultSet.next()) {
                return -1;
            } else {
                return resultSet.getLong("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

}
