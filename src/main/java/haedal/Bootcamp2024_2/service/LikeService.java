package haedal.Bootcamp2024_2.service;

import haedal.Bootcamp2024_2.domain.Like;
import haedal.Bootcamp2024_2.domain.Post;
import haedal.Bootcamp2024_2.domain.User;
import haedal.Bootcamp2024_2.dto.response.UserSimpleResponseDto;
import haedal.Bootcamp2024_2.repository.LikeRepository;
import haedal.Bootcamp2024_2.repository.PostRepository;
import haedal.Bootcamp2024_2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public void likePost(User currentUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        if (likeRepository.existsByUserAndPost(currentUser, post)) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(currentUser, post);
        likeRepository.save(like);
    }

    public void unlikePost(User user, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));

        likeRepository.delete(like);
    }

    public Page<UserSimpleResponseDto> getUsersWhoLikedPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        Page<Like> likes = likeRepository.findByPost(post, pageable);
        return likes.map(like -> new UserSimpleResponseDto(
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getUser().getUserImage(),
                like.getUser().getName()
        ));
    }
}