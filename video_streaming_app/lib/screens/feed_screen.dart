import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/video_provider.dart';
import '../widgets/video_card.dart';

class FeedScreen extends StatefulWidget {
  @override
  _FeedScreenState createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    // Cargar videos al iniciar
    Future.microtask(
      () => Provider.of<VideoProvider>(context, listen: false).fetchVideos(),
    );
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.transparent,
        title: Text(
          'VideoFeed',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
        ),
        centerTitle: true,
      ),
      body: Consumer<VideoProvider>(
        builder: (ctx, videoProvider, child) {
          if (videoProvider.isLoading) {
            return Center(
              child: CircularProgressIndicator(
                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
              ),
            );
          } else if (videoProvider.error != null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.error_outline, size: 48, color: Colors.red),
                  SizedBox(height: 16),
                  Text(
                    'Error: ${videoProvider.error}',
                    style: TextStyle(color: Colors.red),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () => videoProvider.fetchVideos(),
                    child: Text('Reintentar'),
                  ),
                ],
              ),
            );
          } else if (videoProvider.videos.isEmpty) {
            return Center(
              child: Text(
                'No hay videos disponibles',
                style: TextStyle(color: Colors.white, fontSize: 16),
              ),
            );
          } else {
            return PageView.builder(
              scrollDirection: Axis.vertical, // Scroll vertical como TikTok
              controller: _pageController,
              itemCount: videoProvider.videos.length,
              onPageChanged: (int page) {
                setState(() {
                  _currentPage = page;
                });
              },
              itemBuilder: (context, index) {
                final video = videoProvider.videos[index];
                return VideoCard(
                  video: video,
                  videoUrl: videoProvider.getVideoStreamUrl(video.id),
                  isActive: _currentPage == index,
                );
              },
            );
          }
        },
      ),
    );
  }
}
