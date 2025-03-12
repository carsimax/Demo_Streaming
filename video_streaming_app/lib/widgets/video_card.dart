import 'package:flutter/material.dart';
import '../models/video.dart';
import 'video_player_widget.dart';

class VideoCard extends StatelessWidget {
  final Video video;
  final String videoUrl;
  final bool isActive;

  const VideoCard({
    Key? key,
    required this.video,
    required this.videoUrl,
    required this.isActive,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Stack(
      fit: StackFit.expand,
      children: [
        // Reproductor de video
        VideoPlayerWidget(videoUrl: videoUrl, isActive: isActive),

        // Overlay con información del video
        Positioned(
          left: 10,
          right: 10,
          bottom: 20,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                video.title,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  shadows: [
                    Shadow(
                      blurRadius: 10.0,
                      color: Colors.black,
                      offset: Offset(2.0, 2.0),
                    ),
                  ],
                ),
              ),
              SizedBox(height: 8),
              Text(
                video.description,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  shadows: [
                    Shadow(
                      blurRadius: 10.0,
                      color: Colors.black,
                      offset: Offset(2.0, 2.0),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),

        // Botones laterales (like, comentario, etc.)
        Positioned(
          right: 10,
          bottom: 80,
          child: Column(
            children: [
              _buildActionButton(Icons.favorite, '0'),
              SizedBox(height: 15),
              _buildActionButton(Icons.comment, '0'),
              SizedBox(height: 15),
              _buildActionButton(Icons.share, ''),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildActionButton(IconData icon, String label) {
    return Column(
      children: [
        Icon(icon, color: Colors.white, size: 30),
        if (label.isNotEmpty)
          Text(label, style: TextStyle(color: Colors.white, fontSize: 12)),
      ],
    );
  }
}
