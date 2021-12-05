<?php 
    $page = 'index';
?>
<html>
    <head>
        <title>Original | Dashboard</title>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="icon" type="image/png" href="https://i.imgur.com/W6F7QEN.png" sizes="32x32" />
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link rel="stylesheet" href="assets/css/theme.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/MaterialDesign-Webfont/6.2.95/css/materialdesignicons.min.css" integrity="sha512-tYefTUoWKpCvU14EKu/b/VxnSaT3+1ZBlL0BhTPSYCiqsMTbGylad3l6HCr9i4vpsDgJIFke+LQBq3MCXko6Qw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.2/css/all.min.css" integrity="sha512-HK5fgLBL+xu6dm/Ii3z4xhlSUyZgTT9tuc/hSrtw6uzJOvgRr2a9jyxxT1ely+B+xFAmJKVSTbpM/CuL7qxO8w==" crossorigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Karla:ital,wght@0,200;0,300;0,400;0,500;0,600;0,700;0,800;1,800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Kanit:ital,wght@0,800;0,900;1,800&display=swap" rel="stylesheet">
    </head>
    <body>
        <div id="page">
            <div class="page-sidebar">
                <div class="nav-content">
                    <div id="logo">
                        <a href="index.php"><img src="https://i.imgur.com/5MjoUuh.png"></a>
                    </div>
                    <div id="sidebar-links">
                        <ul>
                            <li class="active"><a href="index.php"><i class="mdi mdi-home"></i> <span class="text">Home</span></a></li>
                            <li><a href="online.php"><i class="mdi mdi-account-group"></i> <span class="text">Online Players</a></span></li>
                            <li><a href="staff.php"><i class="mdi mdi-shield"></i> <span class="text">Staff</a></span></li>
                            <li><a href="complaints.php"><i class="mdi mdi-alert-circle"></i> <span class="text">Complaints</a></span></li>
                            <li><a href="unban.php"><i class="mdi mdi-ticket"></i> <span class="text">Unban Requests</a></span></li>
                            <li><a href="bugs.php"><i class="mdi mdi-bug-outline"></i> <span class="text">Bugs</a></span></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="page-content">
                <div class="page-top-bar">
                    <div class="container">
                        <div class="left">
                            <a href="#"><i class="mdi mdi-discord"></i></a>
                            <a href="#"><i class="fab fa-tiktok"></i></a>
                            <a href="#"><i class="mdi mdi-instagram"></i></a>
                            <a href="#"><i class="mdi mdi-youtube"></i></a>
                            <a href="#"><i class="mdi mdi-twitter"></i></a>
                        </div>
                        <div class="right">
                            <a href="#" class="search-btn"><i class="mdi mdi-magnify"></i></a>
                            <a href="login.php" class="top-btn login">
                                Login
                            </a>
                        </div>
                    </div>
                </div>
                <div class="content-wrap">
                    <div class="container">
                        <div class="panel-grid">
                            <div class="panel">
                                <small>Donators</small>
                                <h2>1263</h2>
                            </div>
                            <div class="panel">
                                <small>Registered Players</small>
                                <h2>5213</h2>
                            </div>
                            <div class="panel">
                                <small>Online Players</small>
                                <h2>345</h2>
                            </div>
                        </div>
                        <div class="panel-table">
                            <table>
                                <tr>
                                    <th>User</th>
                                    <th class="hidden-mobile">Details</th>
                                    <th class="hidden-mobile">Location</th>
                                    <th class="hidden-xs">Status</th>
                                </tr>
                                <?php
                                    $arr = array(1, 2, 3, 4, 5, 6);
                                ?>
                                <?php foreach ($arr as &$value) { ?>
                                <tr>
                                    <td class="user"><a href="#"><img class="hidden-mobile" src="https://crafatar.com/avatars/696a82ce41f44b51aa31b8709b8686f0?size=32"> Alfreds Futterkiste</a></td>
                                    <td class="hidden-mobile">No data</td>
                                    <td class="hidden-mobile">Germany</td>
                                    <td class="hidden-xs"><span class="label">Complete</span></td>
                                </tr>
                                <?php } ?>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js" integrity="sha512-894YE6QWD5I59HgZOGReFYm4dnWc1Qt5NtvYSaNcOP+u1T9qYdvdihz0PPSiiqn/+/3e7Jo4EaG7TubfWGUrMQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    </body>
</html>